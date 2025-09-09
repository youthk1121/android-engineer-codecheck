package jp.co.yumemi.android.code_check.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailResponse(
    @SerialName("full_name")
    val fullName: String,
    @SerialName("owner")
    val owner: OwnerResponse,
    @SerialName("language")
    val language: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Long,
    @SerialName("subscribers_count")
    val subscribersCount: Long,
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