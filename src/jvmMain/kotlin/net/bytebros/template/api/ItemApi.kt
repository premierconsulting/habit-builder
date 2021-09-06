package net.bytebros.template.api

import com.soywiz.klock.DateTime
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.bytebros.template.dbQuery
import net.bytebros.template.entities.EffortEntity
import net.bytebros.template.models.Item
import net.bytebros.template.models.ItemUpdate
import net.bytebros.template.models.NewItem
import net.bytebros.template.services.ItemService

@Serializable
data class Effort(
    val name: String,
    @Contextual
    val replenishedAt: DateTime)

fun Routing.itemApi(itemService: ItemService) {
    route("api") {
        get("efforts") {
            val efforts = dbQuery {
                EffortEntity.all().map {
                    Effort(
                        it.item.name,
                        it.replenishedAt
                    )
                }
            }
            call.respond(efforts)
        }
        route("items") {
            get {
                val items = itemService.getItems()
                call.respond(items)
            }
            post {
                val newItem = call.receive<Item>()
                val item = itemService.saveItem(newItem)
                call.respond(item.copy(replenishedAt = DateTime.now()))
            }
            route("{id}") {
                get {
                    val item = call.parameters["id"]?.let { id ->
                        itemService.getItemById(id)
                    } ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(item)
                }
                put {
                    val id = call.parameters["id"]
                        ?: return@put call.respond(HttpStatusCode.NotFound)
                    val itemUpdate = call.receive<ItemUpdate>()
                    val item = itemService.updateItem(id, itemUpdate)
                    call.respond(item)
                }
            }
        }
    }
}