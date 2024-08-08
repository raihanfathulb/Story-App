package com.example.storyapp_kotlin.view.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp_kotlin.adapter.StoryListAdapter
import com.example.storyapp_kotlin.data.StoryRepository
import com.example.storyapp_kotlin.data.api.Story
import com.example.storyapp_kotlin.utils.DataDummy
import com.example.storyapp_kotlin.utils.MainDispatcherRule
import com.example.storyapp_kotlin.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyNewStories()
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)
        val expectedStories = flowOf(data)
        val token = "ini token"
        Mockito.`when`(userRepository.getStory(token)).thenReturn(expectedStories)

        val storyViewModel = MainViewModel(userRepository)
        val actualStory: PagingData<Story> = storyViewModel.getStories(token).asLiveData().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when Get Stories Should Not Null and Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStories = flowOf(data)
        val token = "ini token"
        Mockito.mockStatic(Log::class.java)
        Mockito.`when`(userRepository.getStory(token)).thenReturn(expectedStories)
        val storyViewModel = MainViewModel(userRepository)
        val actualStories: PagingData<Story> = storyViewModel.getStories(token).first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)
        assertEquals(0, differ.snapshot().size)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
        companion object {
            fun snapshot(items: List<Story>): PagingData<Story> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int? {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }



}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}