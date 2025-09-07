package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val name: String,
    val ownerIconUrl: String?,
    private val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable {
    fun getLanguageText(context: Context): String {
        return context.getString(R.string.written_language, language)
    }
}