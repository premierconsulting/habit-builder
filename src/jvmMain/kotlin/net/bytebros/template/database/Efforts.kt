package net.bytebros.template.database

import com.soywiz.klock.DateTime
import net.bytebros.template.models.DateTimeSerializer.dateFormat
import org.jetbrains.exposed.dao.id.UUIDTable

object Efforts: UUIDTable() {
    val item = reference("item", Items)
    val timestamp = varchar("timestamp", 20).default(DateTime.now().format(dateFormat))
}