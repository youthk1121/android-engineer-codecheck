package jp.co.yumemi.android.code_check

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = DetailFragmentArgs.fromSavedStateHandle(savedStateHandle)

    val uiState = MutableStateFlow(DetailUiState(args.detail)).asStateFlow()
    val searchDate = args.searchDate

}

data class DetailUiState(
    val detail: RepositoryDetail
)