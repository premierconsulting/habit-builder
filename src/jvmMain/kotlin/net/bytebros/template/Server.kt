package net.bytebros.template

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import net.bytebros.template.api.itemApi
import net.bytebros.template.database.Efforts
import net.bytebros.template.database.Items
import net.bytebros.template.models.format
import net.bytebros.template.repositories.ItemRepository
import net.bytebros.template.services.impl.ItemServiceImpl
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager

fun Application.module() {
    install(ContentNegotiation) {
        json(format)
    }
    val itemsRepository = ItemRepository()
    val itemsService = ItemServiceImpl(itemsRepository)
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver",)
    dbQuery {
        SchemaUtils.create(Items, Efforts)
    }
    routing {
        itemApi(itemsService)
        static {
            resources("app")
            defaultResource("app/index.html")
        }
    }
}

fun <T> dbQuery(db: Database? = null, statement: Transaction.() -> T): T =
    transaction(db.transactionManager.defaultIsolationLevel, db.transactionManager.defaultRepetitionAttempts, db) {
        addLogger(StdOutSqlLogger)
        statement()
    }

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)