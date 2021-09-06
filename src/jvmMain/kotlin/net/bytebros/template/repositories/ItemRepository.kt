package net.bytebros.template.repositories

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import net.bytebros.template.dbQuery
import net.bytebros.template.entities.EffortEntity
import net.bytebros.template.entities.ItemEntity
import net.bytebros.template.models.Item
import net.bytebros.template.models.ItemUpdate
import net.bytebros.template.models.NewItem
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ItemRepository {
    fun getItems() = dbQuery {
        println("Getting Items")
        ItemEntity.all().toList()
    }
    fun getItemById(id: UUID): ItemEntity = dbQuery {
        println("Getting Item by ID")
        ItemEntity[id]
    }
    fun saveItem(newItem: Item) = dbQuery {
        println("Saving Item")
        val item = ItemEntity.new {
            name = newItem.name
            duration = newItem.duration
        }
        EffortEntity.new {
            this.item = item
            replenishedAt = DateTime.now() - item.duration
        }
        item
    }
    fun updateItem(id: UUID, itemUpdate: ItemUpdate) = dbQuery {
        println("Updating Item")
        val item = ItemEntity[id].also {
            it.name = itemUpdate?.name ?: it.name
            it.duration = itemUpdate?.duration ?: it.duration
        }
        if (itemUpdate.replenishedAt != null) {
            EffortEntity.new {
                this.item = item
                replenishedAt = itemUpdate.replenishedAt
            }
        }
        ItemEntity[id]
    }
}