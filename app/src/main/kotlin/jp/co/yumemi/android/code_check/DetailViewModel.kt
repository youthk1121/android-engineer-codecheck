package jp.co.yumemi.android.code_check

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import jp.co.yumemi.android.code_check.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

class DetailViewModel(
    private val gitHubRepository: GitHubRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = DetailFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val searchDate = args.searchDate

    fun onViewCreated() {
        if (_uiState.value !is DetailUiState.Success) {
            viewModelScope.launch {
                fetchDetail()
            }
        }
    }

    private suspend fun fetchDetail() {
        _uiState.value = DetailUiState.Loading
        val resp = try {
            gitHubRepository.detail(args.url)
        } catch (e: IOException) {
            _uiState.value = DetailUiState.Error
            return
        }
        val detail = RepositoryDetail(
            name = resp.fullName,
            ownerIconUrl = resp.owner.avatarUrl,
            language = resp.language.orEmpty(),
            stargazersCount = resp.stargazersCount,
            watchersCount = resp.subscribersCount,
            forksCount = resp.forksCount,
            openIssuesCount = resp.openIssuesCount,
        )
        _uiState.value = DetailUiState.Success(detail)
    }

    companion object {
        fun provideFactory(
            gitHubRepository: GitHubRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null
        ) = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return DetailViewModel(gitHubRepository, handle) as T
            }
        }

    }
}

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val detail: RepositoryDetail) : DetailUiState
    object Error : DetailUiState
}