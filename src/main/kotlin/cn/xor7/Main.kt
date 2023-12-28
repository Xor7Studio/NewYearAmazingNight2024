package cn.xor7

import cn.zhxu.okhttps.HTTP
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@Volatile
private var cacheData: String = "[]"
private val http = HTTP.builder().build()
private val url = File("API_URL").readText()

fun main() {
    println("starting cache server...")

    Timer("request", true).scheduleAtFixedRate(0, 100) {
        http.cancelAll()
        cacheData = http.sync(url).get().body.toString()
    }

    embeddedServer(Netty, 8000) {
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
        get("/data") {
            call.respondText(
                text = cacheData,
                contentType = ContentType.Application.Json
            )
        }
    }
}