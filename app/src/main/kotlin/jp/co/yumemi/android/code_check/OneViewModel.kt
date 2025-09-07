/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import androidx.lifecycle.ViewModel
import jp.co.yumemi.android.code_check.repository.SearchRepository

/**
 * OneFragment で使う
 */
class OneViewModel : ViewModel() {

    private val searchRepository = SearchRepository()

    // 検索結果
    suspend fun searchResults(inputText: String): List<Item> {
        return searchRepository.search(inputText).map { responseItem ->
            Item(
                name = responseItem.name,
                ownerIconUrl = responseItem.ownerIconUrl,
                language = responseItem.language,
                stargazersCount = responseItem.stargazersCount,
                watchersCount = responseItem.watchersCount,
                forksCount = responseItem.forksCount,
                openIssuesCount = responseItem.openIssuesCount,
            )
        }
    }
}