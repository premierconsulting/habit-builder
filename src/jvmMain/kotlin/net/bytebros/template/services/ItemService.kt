package net.bytebros.template.services

import net.bytebros.template.models.Item
import net.bytebros.template.models.ItemUpdate
import net.bytebros.template.models.NewItem

interface ItemService {
    fun getItems(): List<Item>
    fun getItemById(id: String): Item?
    fun saveItem(newItem: Item): Item
    fun updateItem(id: String, itemUpdate: ItemUpdate): Item
}