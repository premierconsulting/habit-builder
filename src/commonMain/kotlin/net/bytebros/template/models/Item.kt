package net.bytebros.template.models

import com.soywiz.klock.*
import dev.fritz2.identification.inspect
import dev.fritz2.lenses.IdProvider
import dev.fritz2.lenses.Lenses
import dev.fritz2.resource.Resource
import dev.fritz2.validation.ValidationMessage
import dev.fritz2.validation.Validator
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.math.roundToInt

@Lenses
@Serializable
data class Item(
    val id: String = "",
    val name: String,
    @Contextual
    val replenishedAt: DateTime = DateTime.now(),
    @Contextual
    val duration: TimeSpan = 3.minutes,
) {
    val depletesAt: DateTime
        get() = replenishedAt + duration

    fun progress(now: DateTime): Int {
        val timeLeft = depletesAt.minus(now)
        val progress = (timeLeft.milliseconds / duration.milliseconds) * 100
        return maxOf(minOf(progress.roundToInt(), 100), 0)
    }
}

@Serializable
data class ItemUpdate(
    val name: String? = null,
    @Contextual
    val replenishedAt: DateTime? = null,
    @Contextual
    val duration: TimeSpan? = null
)

data class ItemMessage(val id: String, val text: String) : ValidationMessage {
    override fun isError(): Boolean = true
}

class ItemValidator : Validator<Item, ItemMessage, Unit>() {

    override fun validate(data: Item, metadata: Unit): List<ItemMessage> {
        val msgs = mutableListOf<ItemMessage>()
        val inspector = inspect(data, "items")

        val textInspector = inspector.sub(L.Item.name)

        if (textInspector.data.isEmpty()) msgs.add(
            ItemMessage(
                textInspector.id,
                "Name cannot be empty"
            )
        )

        return msgs
    }
}

object ItemResource : Resource<Item, String> {
    override val idProvider: IdProvider<Item, String> = Item::id

    override fun deserialize(source: String): Item = format.decodeFromString(Item.serializer(), source)
    override fun serialize(item: Item): String = format.encodeToString(Item.serializer(), item)
    override fun deserializeList(source: String): List<Item> = format.decodeFromString(ListSerializer(Item.serializer()), source)
    override fun serializeList(items: List<Item>): String = format.encodeToString(ListSerializer(Item.serializer()), items)
}

object DateTimeSerializer : KSerializer<DateTime> {
    val dateFormat: DateFormat = DateFormat.FORMAT1
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: DateTime) = encoder.encodeString(value.format(dateFormat))
    override fun deserialize(decoder: Decoder): DateTime = dateFormat.parseUtc(decoder.decodeString())
}

object TimeSpanSerializer : KSerializer<TimeSpan> {
    val dateFormat: DateFormat = DateFormat.FORMAT1
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: TimeSpan) = encoder.encodeString(value.milliseconds.toString())
    override fun deserialize(decoder: Decoder): TimeSpan = TimeSpan(decoder.decodeString().toDouble())
}

val module = SerializersModule {
    contextual(DateTimeSerializer)
    contextual(TimeSpanSerializer)
}

val format = Json {
    serializersModule = module
    ignoreUnknownKeys = true
}