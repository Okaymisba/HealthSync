package com.example.plugins

import com.example.User
import com.example.retrieveUsernameFromDatabase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        staticResources("/static", "static")

        get("/login") {
            val file = File("src/main/resources/static/index.html")
            if (file.exists()) {
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound, "File not found")
            }
        }

        post("/login") {
            val parameters = call.receiveParameters()
            val username = parameters["username"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val password = parameters["password"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val user = retrieveUsernameFromDatabase(username)


            if (user == password) {
                val file = File("src/main/resources/static/mainPage.html")
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            }

        }
    }
}
