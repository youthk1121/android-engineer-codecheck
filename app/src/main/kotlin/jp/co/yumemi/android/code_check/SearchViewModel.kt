/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.yumemi.android.code_check.repository.GitHubRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import java.util.Date

/**
 * SearchFragment で使う
 */
class SearchViewModel : ViewModel() {

    private val gitHubRepository = GitHubRepository()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Init)
    val searchUiState = _searchUiState.asStateFlow()

    private var searchJob: Job? = null

    fun onImeSearch(query: String) {
        searchJob?.cancel()
        _searchUiState.value = SearchUiState.Loading
        searchJob = viewModelScope.launch {
            val responseList = try {
                gitHubRepository.search(query)
            } catch (e: IOException) {
                Log.e("検索結果", "検索エラー", e)
                _searchUiState.value = SearchUiState.Error
                return@launch
            }
            val fetchDate = Date()
            val items = responseList.map { responseItem ->
                Item(
                    name = responseItem.fullName,
                    ownerIconUrl = responseItem.owner.avatarUrl,
                    language = responseItem.language.orEmpty(),
                    stargazersCount = responseItem.stargazersCount,
                    watchersCount = responseItem.watchersCount,
                    forksCount = responseItem.forksCount,
                    openIssuesCount = responseItem.openIssuesCount,
                    fetchDate = fetchDate
                )
            }
            _searchUiState.value = SearchUiState.Results(items)
        }
    }

    fun onShowEmptyResult() {
        _searchUiState.value = SearchUiState.Init
    }

    fun onShowErrorResult() {
        _searchUiState.value = SearchUiState.Init
    }
}

sealed interface SearchUiState {
    object Init: SearchUiState

    object Loading : SearchUiState

    data class Results(
        val itemList: List<Item>
    ) : SearchUiState

    object Error: SearchUiState
}