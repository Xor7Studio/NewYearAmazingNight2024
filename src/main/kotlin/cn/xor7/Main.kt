package cn.xor7

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    println("starting cache server...")
    embeddedServer(Netty, 8080) {
        setupCacheServer()
    }.start(wait = true)
}

private fun Application.setupCacheServer() {
    install(CORS) {
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true
        anyHost()
    }

    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}