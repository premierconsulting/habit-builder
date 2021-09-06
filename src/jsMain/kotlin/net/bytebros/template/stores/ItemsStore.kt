package net.bytebros.template.stores

import com.soywiz.klock.DateTime
import dev.fritz2.binding.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repositories.localstorage.localStorageQuery
import dev.fritz2.repositories.rest.restQuery
import kotlinx.browser.document
import kotlinx.browser.window
import net.bytebros.template.models.Item
import net.bytebros.template.models.ItemResource
import net.bytebros.template.models.ItemValidator

data class ItemQuery(val namePrefix: String? = null)

val itemValidator = ItemValidator()

object ItemsStore: RootStore<List<Item>>(listOf(), "items") {
    private var _timeoutHandler: Int = 0
    private val restRepository = localStorageQuery<Item, String, Unit>(ItemResource, "items-")

    private val query = handle { restRepository.query(Unit) }

    val save = handle<Item> { items, newItem ->
        if (itemValidator.isValid(newItem, Unit)) {
            restRepository.addOrUpdate(items, newItem)
        } else {
            items
        }
    }

    val tick = handleAndEmit<DateTime> { state ->
        emit(DateTime.now())
        state
    }

    init {
        query()
        _timeoutHandler = window.setInterval({ tick() }, 1000)
    }
}
