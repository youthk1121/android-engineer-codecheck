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
    @SerialName("url")
    val url: String
)