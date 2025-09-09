package jp.co.yumemi.android.code_check.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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