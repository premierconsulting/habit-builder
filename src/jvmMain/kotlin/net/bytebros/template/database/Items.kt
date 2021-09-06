package net.bytebros.template.database

import com.soywiz.klock.days
import org.jetbrains.exposed.dao.id.UUIDTable

object Items: UUIDTable() {
    val name = varchar("name", 50)
    val duration = double("duration").default(1.days.milliseconds)
}