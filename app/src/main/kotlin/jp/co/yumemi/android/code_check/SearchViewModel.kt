/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
class SearchViewModel(
    private val gitHubRepository: GitHubRepository,
    private val getCurrentDate: () -> Date = { Date() }
) : ViewModel() {

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
            val fetchDate = getCurrentDate()
            val items = responseList.map { responseItem ->
                Item(
                    name = responseItem.fullName,
                    url = responseItem.url,
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

    companion object {
        fun provideFactory(
            gitHubRepository: GitHubRepository
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SearchViewModel(gitHubRepository)
                }
            }
        }
    }
}

sealed interface SearchUiState {
    object Init : SearchUiState

    object Loading : SearchUiState

    data class Results(
        val itemList: List<Item>
    ) : SearchUiState

    object Error : SearchUiState
}