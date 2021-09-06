package net.bytebros.template.entities

import com.soywiz.klock.DateTime
import com.soywiz.klock.minutes
import net.bytebros.template.dbQuery
import net.bytebros.template.models.Item

fun ItemEntity.toItem() = Item(
        id.toString(),
        name,
        replenishedAt() ?: (DateTime.now() - duration),
        duration
    )
