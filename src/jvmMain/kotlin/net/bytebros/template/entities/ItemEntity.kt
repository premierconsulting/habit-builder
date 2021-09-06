package net.bytebros.template.entities

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import net.bytebros.template.database.Efforts
import net.bytebros.template.database.Items
import net.bytebros.template.dbQuery
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import java.util.*

class ItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by Items.name
    var duration: TimeSpan by Items.duration.transform({ it.milliseconds }, { TimeSpan(it) })
    val efforts by EffortEntity referrersOn Efforts.item

    fun replenishedAt(): DateTime? = dbQuery {
        println("Getting efforts for item")
        val effort = EffortEntity
            .find { Efforts.item eq this@ItemEntity.id }
            .orderBy(Efforts.timestamp to SortOrder.DESC)
            .firstOrNull()

        effort?.replenishedAt
    }

    companion object : UUIDEntityClass<ItemEntity>(Items)
}