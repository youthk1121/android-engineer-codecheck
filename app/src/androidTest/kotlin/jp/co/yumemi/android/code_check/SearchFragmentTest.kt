package jp.co.yumemi.android.code_check

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.idling.concurrent.IdlingThreadPoolExecutor
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import jp.co.yumemi.android.code_check.repository.GitHubRepository
import jp.co.yumemi.android.code_check.repository.ItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("NonAsciiCharacters", "TestFunctionName")
@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    //    @get:Rule
//    val activityRule = ActivityScenarioRule(TopActivity::class.java)
//    private val testDispatcher = StandardTestDispatcher()

    private lateinit var idlingResource: IdlingThreadPoolExecutor
    private lateinit var dispatcher: ExecutorCoroutineDispatcher

    @Before
    fun setUp() {
        val threadFactory = Executors.defaultThreadFactory()
        val poolName = "testExecutor"
        val corePoolSize = 1 // アプリケーションの要件に合わせて調整
        val maximumPoolSize = 1 // アプリケーションの要件に合わせて調整
        val keepAliveTime = 1L // アプリケーションの要件に合わせて調整
        val timeUnit = TimeUnit.SECONDS
        val workQueue = LinkedBlockingQueue<Runnable>() // 通常はデフォルトで良い

        idlingResource = IdlingThreadPoolExecutor(
            poolName,
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            timeUnit,
            workQueue,
            threadFactory)

        // idlingResourceを登録する
        IdlingRegistry.getInstance().register(idlingResource)
        
        dispatcher = idlingResource.asCoroutineDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {

        // idlingResourceの登録を解除する
        IdlingRegistry.getInstance().unregister(idlingResource)
        Dispatchers.resetMain()
    }


    @Test
    fun kotlinで検索_検索結果をリスト表示() {
        val responseList = listOf(
            ItemResponse(
                fullName = "JetBrains/kotlin",
                url = "https://api.github.com/repos/JetBrains/kotlin/subscribers"
            )
        )
        val gitHubRepository: GitHubRepository = mock {
            onBlocking { search("kotlin") } doReturn responseList
        }

        val scenario = launchFragmentInContainer(initialState = Lifecycle.State.INITIALIZED) {
            SearchFragment(gitHubRepository)
        }

        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.searchInputText))
            .perform(typeText("kotlin"))
            .perform(pressImeActionButton())

        val recyclerViewId = R.id.recyclerView
        // RecyclerViewの0番目のアイテムにスクロールし、そのアイテム内のrepositoryNameViewのテキストを検証
        onView(withId(recyclerViewId))
            .perform(scrollToPosition<CustomAdapter.ViewHolder>(0))
            .check(matches(atPosition(0, hasDescendant(allOf(withId(R.id.repositoryNameView), withText("JetBrains/kotlin"))))))
        
    }

    // RecyclerViewの特定の位置のアイテムを検証するためのカスタムMatcher
    private fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view !is RecyclerView) {
                    return false
                }
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}