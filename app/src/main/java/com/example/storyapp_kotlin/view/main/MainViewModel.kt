package com.example.storyapp_kotlin.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp_kotlin.data.StoryRepository
import com.example.storyapp_kotlin.data.UserRepository
import com.example.storyapp_kotlin.data.api.Story
import com.example.storyapp_kotlin.data.api.StoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            storyRepository.saveUserToken(token)
        }
    }


    fun getStories(token: String): Flow<PagingData<Story>> {
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }



}