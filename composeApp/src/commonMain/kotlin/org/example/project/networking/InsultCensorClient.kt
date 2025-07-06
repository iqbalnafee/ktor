package org.example.project.networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import org.example.project.data.CensoredText
import org.example.project.util.NetworkError
import org.example.project.util.Result

class InsultCensorClient(
    private val httpClient: HttpClient
) {

    /*Because it does network work, which can take time (like 100ms or 2s or even more).
    We don't want to block the main thread (which would freeze the UI).

    In Kotlin, we mark such functions as suspend.
    This means:
    It can pause and wait (like await in JavaScript or Python await).
    The function will return later when it is done, without blocking the app*/

    suspend fun censorWords(uncensored: String): Result<String, NetworkError>{
        val response = try {
            httpClient.get(
                urlString = "https://www.purgomalum.com/service/json"
            ) {
                parameter("text", uncensored)
            }
        } catch(e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> {
                val censoredText = response.body<CensoredText>()
                Result.Success(censoredText.result)
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}



