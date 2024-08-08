package com.example.storyapp_kotlin.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp_kotlin.data.StoryRepository
import com.example.storyapp_kotlin.data.UserRepository
import com.example.storyapp_kotlin.data.pref.UserPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository:StoryRepository,
    private val authRepository: UserRepository,
    ):ViewModel() {


    fun getStoriesWithLocation(token: String) = repository.getStoriesWithLocation(token)

    fun getAuthToken(): Flow<String?> = authRepository.getUserToken()

}