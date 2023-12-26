package cn.xor7

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@Volatile
private var cacheData: String = "[]"

fun main() {
    println("starting cache server...")

    Timer("request", true).scheduleAtFixedRate(5000, 0) {
        cacheData = "[]"
    }

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
            call.respondText(
                text = cacheData,
                contentType = ContentType.Application.Json
            )
        }
    }
}