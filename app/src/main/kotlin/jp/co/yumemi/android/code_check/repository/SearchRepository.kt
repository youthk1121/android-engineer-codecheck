package jp.co.yumemi.android.code_check.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import org.json.JSONObject

class SearchRepository() {
    suspend fun search(query: String): List<SearchResponse> {
        val client = HttpClient(Android)

        val response: HttpResponse = client.get(SEARCH_URL) {
            header(ACCEPT_HEADER_NAME, ACCEPT_HEADER_VALUE)
            parameter(QUERY_PARAM_NAME, query)
        }

        val jsonBody = JSONObject(response.receive<String>())

        val jsonItems = jsonBody.optJSONArray("items") ?: return emptyList()

        val responseList = mutableListOf<SearchResponse>()

        /**
         * アイテムの個数分ループする
         */
        for (i in 0 until jsonItems.length()) {
            val jsonItem = jsonItems.optJSONObject(i)
            val name = jsonItem.optString("full_name")
            val ownerIconUrl = jsonItem.optJSONObject("owner")?.optString("avatar_url")
            val language = jsonItem.optString("language")
            val stargazersCount = jsonItem.optLong("stargazers_count")
            val watchersCount = jsonItem.optLong("watchers_count")
            val forksCount = jsonItem.optLong("forks_count")
            val openIssuesCount = jsonItem.optLong("open_issues_count")

            responseList.add(
                SearchResponse(
                    name = name,
                    ownerIconUrl = ownerIconUrl,
                    language = language,
                    stargazersCount = stargazersCount,
                    watchersCount = watchersCount,
                    forksCount = forksCount,
                    openIssuesCount = openIssuesCount
                )
            )
        }

        return responseList.toList()
    }

    companion object {
        private const val SEARCH_URL = "https://api.github.com/search/repositories"
        private const val ACCEPT_HEADER_NAME = "Accept"
        private const val ACCEPT_HEADER_VALUE = "application/vnd.github.v3+json"
        private const val QUERY_PARAM_NAME = "q"
    }
}

data class SearchResponse(
    val name: String,
    val ownerIconUrl: String?,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
)