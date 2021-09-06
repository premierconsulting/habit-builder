package net.bytebros.template.entities

import com.soywiz.klock.*
import net.bytebros.template.database.Efforts
import net.bytebros.template.models.DateTimeSerializer
import net.bytebros.template.models.DateTimeSerializer.dateFormat
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

fun String.toDateTime() = dateFormat.parse(this)

class EffortEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    var item by ItemEntity referencedOn Efforts.item
    var replenishedAt: DateTime by Efforts.timestamp.transform({ it.format(dateFormat) }, { dateFormat.parseUtc(it) })

    companion object : UUIDEntityClass<EffortEntity>(Efforts)
}