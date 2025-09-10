package jp.co.yumemi.android.code_check

import androidx.lifecycle.SavedStateHandle
import jp.co.yumemi.android.code_check.repository.DetailResponse
import jp.co.yumemi.android.code_check.repository.GitHubRepository
import jp.co.yumemi.android.code_check.repository.OwnerResponse
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.util.Date


@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("NonAsciiCharacters", "TestFunctionName")
@RunWith(RobolectricTestRunner::class)
class DetailViewModelTest {
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
    fun onViewCreated_リクエスト成功_詳細表示() = runTest(testDispatcher.scheduler) {
        val repoUrl = "https://api.github.com/repos/JetBrains/kotlin"
        val mockResponse = DetailResponse(
            fullName = "JetBrains/kotlin",
            owner = OwnerResponse(avatarUrl = "https://avatars.githubusercontent.com/u/878437?v=4"),
            language = "Kotlin",
            stargazersCount = 40000,
            subscribersCount = 2000,
            forksCount = 5000,
            openIssuesCount = 100
        )
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { detail(repoUrl) } doReturn mockResponse
        }
        val savedStateHandle = SavedStateHandle().apply {
            set("url", repoUrl)
            set("searchDate", Date())
        }
        val viewModel = DetailViewModel(gitHubRepository, savedStateHandle)

        viewModel.onViewCreated()
        advanceUntilIdle()

        val expectedDetail = RepositoryDetail(
            name = "JetBrains/kotlin",
            ownerIconUrl = "https://avatars.githubusercontent.com/u/878437?v=4",
            language = "Kotlin",
            stargazersCount = 40000,
            watchersCount = 2000,
            forksCount = 5000,
            openIssuesCount = 100
        )
        assertEquals(DetailUiState.Success(expectedDetail), viewModel.uiState.value)
    }

    @Test
    fun onViewCreated_リクエスト失敗_エラー表示() = runTest(testDispatcher.scheduler) {
        val repoUrl = "https://api.github.com/repos/JetBrains/kotlin"
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { detail(repoUrl) } doAnswer { throw IOException() }
        }
        val savedStateHandle = SavedStateHandle().apply {
            set("url", repoUrl)
            set("searchDate", Date())
        }
        val viewModel = DetailViewModel(gitHubRepository, savedStateHandle)

        viewModel.onViewCreated()
        advanceUntilIdle()

        assertEquals(DetailUiState.Error, viewModel.uiState.value)
    }
}
