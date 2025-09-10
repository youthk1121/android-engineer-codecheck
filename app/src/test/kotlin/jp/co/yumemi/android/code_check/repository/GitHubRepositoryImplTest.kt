package jp.co.yumemi.android.code_check.repository

import kotlinx.coroutines.runBlocking
import org.junit.Test

class GitHubRepositoryImplTest {
    @Test
    fun search() = runBlocking {
        val repository = GitHubRepositoryImpl()
        val results = repository.search("kotlin")
        assert(results.isNotEmpty())
    }

    @Test
    fun detail() = runBlocking {
        val repository = GitHubRepositoryImpl()
        val detail = repository.detail("https://api.github.com/repos/JetBrains/kotlin")
        assert(detail.fullName == "JetBrains/kotlin")
    }
}