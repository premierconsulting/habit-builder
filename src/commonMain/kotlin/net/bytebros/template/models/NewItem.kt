package net.bytebros.template.models

import com.soywiz.klock.TimeSpan
import com.soywiz.klock.days
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class NewItem(
    val name: String,
    @Contextual
    val duration: TimeSpan = 1.days,
)