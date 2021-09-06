package net.bytebros.template.services.impl

import net.bytebros.template.entities.toItem
import net.bytebros.template.models.Item
import net.bytebros.template.models.ItemUpdate
import net.bytebros.template.models.NewItem
import net.bytebros.template.repositories.ItemRepository
import net.bytebros.template.services.ItemService
import java.util.*

class ItemServiceImpl(private val itemRepository: ItemRepository): ItemService {
    override fun getItems() = itemRepository
        .getItems()
        .map { it.toItem() }

    override fun getItemById(id: String): Item = itemRepository
        .getItemById(UUID.fromString(id))
        .toItem()

    override fun saveItem(newItem: Item): Item = itemRepository
        .saveItem(newItem)
        .toItem()

    override fun updateItem(id: String, itemUpdate: ItemUpdate): Item = itemRepository
        .updateItem(UUID.fromString(id), itemUpdate)
        .toItem()
}