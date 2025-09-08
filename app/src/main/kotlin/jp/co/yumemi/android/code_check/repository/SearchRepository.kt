package jp.co.yumemi.android.code_check.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SearchRepository {
    private val serializer = Json { ignoreUnknownKeys = true }

    suspend fun search(query: String): List<ItemResponse> {
        val client = HttpClient(Android)

        val response: HttpResponse = client.get(SEARCH_URL) {
            header(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE)
            parameter(QUERY_PARAM_NAME, query)
        }

        val json = response.receive<String>()
        val searchResponse = serializer.decodeFromString<SearchResponse>(json)
        return searchResponse.items
    }

    companion object {
        private const val SEARCH_URL = "https://api.github.com/search/repositories"
        private const val ACCEPT_HEADER_NAME = "Accept"
        private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        private const val QUERY_PARAM_NAME = "q"
    }
}

@Serializable
data class SearchResponse(
    @SerialName("items")
    val items: List<ItemResponse>
)

@Serializable
data class ItemResponse(
    @SerialName("full_name")
    val fullName: String,
    @SerialName("owner")
    val owner: OwnerResponse,
    @SerialName("language")
    val language: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Long,
    @SerialName("watchers_count")
    val watchersCount: Long,
    @SerialName("forks_count")
    val forksCount: Long,
    @SerialName("open_issues_count")
    val openIssuesCount: Long,
)

@Serializable
data class OwnerResponse(
    @SerialName("avatar_url")
    val avatarUrl: String
)