package jp.co.yumemi.android.code_check.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface GitHubRepository {
    suspend fun search(query: String): List<ItemResponse>

    suspend fun detail(url: String): DetailResponse
}

class GitHubRepositoryImpl : GitHubRepository {
    private val serializer = Json { ignoreUnknownKeys = true }

    override suspend fun search(query: String): List<ItemResponse> {
        val client = HttpClient(Android)

        val response: HttpResponse = client.get(SEARCH_URL) {
            header(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE)
            parameter(QUERY_PARAM_NAME, query)
        }

        val json = response.receive<String>()
        val searchResponse = serializer.decodeFromString<SearchResponse>(json)
        return searchResponse.items
    }

    override suspend fun detail(url: String): DetailResponse {
        val client = HttpClient(Android)

        val response: HttpResponse = client.get(url) {
            header(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE)
        }

        val json = response.receive<String>()
        val detailResponse = serializer.decodeFromString<DetailResponse>(json)
        return detailResponse
    }

    companion object {
        private const val SEARCH_URL = "https://api.github.com/search/repositories"
        private const val ACCEPT_HEADER_NAME = "Accept"
        private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        private const val QUERY_PARAM_NAME = "q"
    }
}