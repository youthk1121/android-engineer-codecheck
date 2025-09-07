/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import jp.co.yumemi.android.code_check.repository.SearchRepository

/**
 * OneFragment で使う
 */
class OneViewModel(application: Application) : AndroidViewModel(application) {

    private val searchRepository = SearchRepository()

    // 検索結果
    suspend fun searchResults(inputText: String): List<Item> {
        return searchRepository.search(inputText).map { responseItem ->
            Item(
                name = responseItem.name,
                ownerIconUrl = responseItem.ownerIconUrl,
                language = getApplication<Application>().getString(
                    R.string.written_language,
                    responseItem.language
                ),
                stargazersCount = responseItem.stargazersCount,
                watchersCount = responseItem.watchersCount,
                forksCount = responseItem.forksCount,
                openIssuesCount = responseItem.openIssuesCount,
            )
        }
    }
}