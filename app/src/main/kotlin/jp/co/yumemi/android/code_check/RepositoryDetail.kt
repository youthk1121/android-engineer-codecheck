package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepositoryDetail(
    val name: String,
    val ownerIconUrl: String?,
    private val language: String,
    private val stargazersCount: Long,
    private val watchersCount: Long,
    private val forksCount: Long,
    private val openIssuesCount: Long
) : Parcelable {
    fun getLanguageText(context: Context): String {
        return context.getString(R.string.written_language, language)
    }
    fun getStarsText(context: Context): String {
        return context.getString(R.string.detail_stars_format, stargazersCount)
    }
    fun getWatchersText(context: Context): String {
        return context.getString(R.string.detail_watchers_format, watchersCount)
    }
    fun getForksText(context: Context): String {
        return context.getString(R.string.detail_forks_format, forksCount)
    }
    fun getOpenIssuesText(context: Context): String {
        return context.getString(R.string.detail_open_issues_format, openIssuesCount)
    }
}
