package net.bytebros.template

import com.soywiz.klock.*
import dev.fritz2.binding.storeOf
import dev.fritz2.binding.watch
import dev.fritz2.dom.html.Keys
import dev.fritz2.dom.html.RenderContext
import dev.fritz2.dom.html.render
import dev.fritz2.dom.states
import dev.fritz2.dom.values
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import net.bytebros.template.formats.Formats
import net.bytebros.template.models.DateTimeSerializer.dateFormat
import net.bytebros.template.models.Item
import net.bytebros.template.models.L
import net.bytebros.template.stores.ItemsStore

@OptIn(ExperimentalCoroutinesApi::class)
fun RenderContext.formSection() {
    val itemStore = storeOf(Item(
        name = "",
        duration = 60.minutes,
    ))

    val nameStore = itemStore.sub(L.Item.name)
    val duration = itemStore.sub(L.Item.duration)
    val quantityStore = storeOf(1)
    val unitStore = storeOf("hours")
    p {
        a("btn btn-danger") {
            attr("data-bs-toggle", "collapse")
            href("#newHabitForm")
            attr("role", "button")
            attr("aria-expanded", "false")
            attr("aria-controls", "newHabitForm")
            +"""New Habit"""
        }
    }
    div("collapse", "newHabitForm") {
        div("card card-body") {
            form("container") {
        div("row g-3 align-items-center") {
            div("col-auto") {
                label("col-form-label") {
                    `for`("name")
                    +"""Habit"""
                }
            }
            div("col-auto") {
                input("form-control", "name") {
                    type("text")
                    name("name")
                    value(nameStore.data)
                    changes.values() handledBy nameStore.update
                }
            }
        }
        div("row g-3 align-items-center") {
            div("col-auto") {
                input("form-control", "quantity") {
                    type("number")
                    name("quantity")
                    value(quantityStore.data.map { it.toString() })
                    changes.values().map { it.toInt() } handledBy quantityStore.update
                }
            }
            div("col-auto form-check") {
                input("form-check-input") {
                    type("radio")
                    name("flexRadioDefault")
                    name("unit")
                    checked(unitStore.data.map { it == "days" })
                    changes.states().mapNotNull { if (it) "days" else null } handledBy unitStore.update
                }
                label("form-check-label") {
                    `for`("radio")
                    +"""Days"""
                }
            }
            div("col-auto form-check") {
                input("form-check-input") {
                    type("radio")
                    name("unit")
                    checked(unitStore.data.map { it == "hours" })
                    changes.states().mapNotNull { if (it) "hours" else null } handledBy unitStore.update
                }
                label("form-check-label") {
                    `for`("radio")
                    +"""Hours"""
                }
            }
            div("col-auto form-check") {
                input("form-check-input") {
                    type("radio")
                    name("unit")
                    checked(unitStore.data.map { it == "minutes" })
                    changes.states().mapNotNull { if (it) "minutes" else null } handledBy unitStore.update
                }
                label("form-check-label") {
                    `for`("radio")
                    +"""Minutes"""
                }
            }
        }
        button("btn btn-primary") {
            type("submit")
            +"""Submit"""
            clicks.preventDefault().map {
                val item = Item(
                    name = nameStore.current,
                    duration = when(unitStore.current) {
                        "days" -> quantityStore.current.days
                        "hours" -> quantityStore.current.hours
                        "minutes" -> quantityStore.current.minutes
                        else -> 1.days
                    })
                nameStore.update("")
                unitStore.update("days")
                quantityStore.update(0)
                item
            } handledBy ItemsStore.save
        }
    }
        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
fun RenderContext.mainSection() {
    section("main container") {
        div("row") {
            div("col") {
                ul("item-list list-unstyled") {
                    ItemsStore.data.renderEach(Item::id) { item ->
                        val itemStore = storeOf(item)
                        itemStore.syncBy(ItemsStore.save)

                        val nameStore = itemStore.sub(L.Item.name)
                        val progressStore = itemStore.data.combine(ItemsStore.tick) { it, now ->
                            it.progress(now)
                        }
                        val progressWidthStore = progressStore.map { progress ->
                            val width = if (progress == 0) 100 else progress
                            "width: $width%"
                        }
                        val depletesStore = itemStore.data.combine(ItemsStore.tick) { it, now ->
                            val duration = if (now > it.depletesAt) (now - it.depletesAt).toTimeString() else (it.depletesAt - now).toTimeString()
                            val prefix = if (now > it.depletesAt) "Depleted for " else "Depletes in "
                            "$prefix $duration"
                        }

                        li("item mb-2") {
                            attr("data-id", itemStore.id)
                            h5 {
                                classMap(progressStore.map {
                                    mapOf(
                                        "text-danger" to (it == 0)
                                    )
                                })
                                nameStore.data.asText()
                                dblclicks.map { itemStore.current.copy(replenishedAt = DateTime.now()) } handledBy itemStore.update
                            }
                            p {
                                depletesStore.asText()
                            }
                            div("progress") {
                                div("progress-bar") {
                                    classMap(
                                        progressStore.map { progress ->
                                            mapOf(
                                                "progress-bar-striped progress-bar-animated" to (progress in 1..100),
                                                "bg-success" to (progress in 50..100),
                                                "bg-warning" to (progress in 20..50),
                                                "bg-danger" to (progress in 0..20),
                                            )
                                        }
                                    )
                                    inlineStyle(progressWidthStore)
                                    attr("role", "progressbar")
                                    attr("role", "progressbar")
                                    attr("aria-valuenow", progressStore)
                                    attr("aria-valuemin", "0")
                                    attr("aria-valuemax", "100")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    render("#target") {
        div("container mt-4") {
            formSection()
            mainSection()
        }
    }
}