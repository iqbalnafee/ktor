// this file uses Ktor for making HTTP network requests.

package org.example.project.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// the below function creates and configures an HTTP client
// HttpClient is the main Ktor class for making HTTP requests.
fun createHttpClient(engine: HttpClientEngine): HttpClient{ // HttpClientEngine tells how HTTP requests will be performed (for example OkHttp for android, Darwin on iOS, etc)
    return HttpClient(engine){
        install(Logging){
            level = LogLevel.ALL
        }

        install(ContentNegotiation){
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    /*Why pass engine?
    Because Compose Multiplatform targets multiple platforms (Android, iOS, Desktop, etc).
    Each platform needs its own HTTP engine. For example:
    val client = createHttpClient(OkHttp.create())
    val client = createHttpClient(Darwin.create()) */

    /*Why do we use install every time?
    Ktor's HttpClient is like a small empty toolbox.
    If we want it to have extra tools, like logging, JSON handling, timeouts, etc.
    we have to explicitly install each tool we need.*/
}