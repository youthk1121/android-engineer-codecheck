package jp.co.yumemi.android.code_check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.yumemi.android.code_check.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gitHubRepository = GitHubRepository()

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

}

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val detail: RepositoryDetail) : DetailUiState
    object Error : DetailUiState
}