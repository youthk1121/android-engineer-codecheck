package jp.co.yumemi.android.code_check

import jp.co.yumemi.android.code_check.repository.GitHubRepository
import jp.co.yumemi.android.code_check.repository.ItemResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("NonAsciiCharacters", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onImeSearch_リクエスト成功_検索結果表示() = runTest(testDispatcher.scheduler) {
        val responseList = listOf(
            ItemResponse(
                fullName = "JetBrains/kotlin",
                url = "https://api.github.com/repos/JetBrains/kotlin/subscribers"
            ),
            ItemResponse(
                fullName = "hussien89aa/KotlinUdemy",
                url = "https://api.github.com/repos/hussien89aa/KotlinUdemy/subscribers"
            )
        )
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { search("kotlin") } doReturn responseList
        }
        val currentDate = Date()
        val viewModel = SearchViewModel(gitHubRepository, getCurrentDate = { currentDate })

        viewModel.onImeSearch("kotlin")
        advanceUntilIdle()

        val expectedState = SearchUiState.Results(
            itemList = listOf(
                Item(
                    name = "JetBrains/kotlin",
                    url = "https://api.github.com/repos/JetBrains/kotlin/subscribers",
                    fetchDate = currentDate
                ),
                Item(
                    name = "hussien89aa/KotlinUdemy",
                    url = "https://api.github.com/repos/hussien89aa/KotlinUdemy/subscribers",
                    fetchDate = currentDate
                )
            )
        )
        assertEquals(expectedState, viewModel.searchUiState.value)
    }


    @Test
    fun onImeSearch_リクエスト失敗_エラー表示() = runTest(testDispatcher.scheduler) {
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { search("kotlin") } doAnswer { throw IOException() }
        }
        val currentDate = Date()
        val viewModel = SearchViewModel(gitHubRepository, getCurrentDate = { currentDate })

        viewModel.onImeSearch("kotlin")
        advanceUntilIdle()

        assertEquals(SearchUiState.Error, viewModel.searchUiState.value)
    }

    @Test
    fun onShowEmptyResult_UiStateをInitに戻す() = runTest(testDispatcher.scheduler) {
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { search(any()) } doReturn emptyList()
        }
        val viewModel = SearchViewModel(gitHubRepository)
        viewModel.onImeSearch("") // 一度状態を空の結果にする
        advanceUntilIdle()

        assertEquals(SearchUiState.Results(emptyList()), viewModel.searchUiState.value)

        viewModel.onShowEmptyResult()

        assertEquals(SearchUiState.Init, viewModel.searchUiState.value)
    }

    @Test
    fun onShowErrorResult_UiStateをInitに戻す() = runTest(testDispatcher.scheduler) {
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { search(any()) } doAnswer { throw IOException() }
        }
        val viewModel = SearchViewModel(gitHubRepository)
        viewModel.onImeSearch("") // 一度状態をエラーにする
        advanceUntilIdle()

        assertEquals(SearchUiState.Error, viewModel.searchUiState.value)

        viewModel.onShowErrorResult()

        assertEquals(SearchUiState.Init, viewModel.searchUiState.value)
    }
}