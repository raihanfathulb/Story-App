package com.example.storyapp_kotlin.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp_kotlin.data.api.ApiService
import com.example.storyapp_kotlin.data.api.FileUploadResponse
import com.example.storyapp_kotlin.data.api.MapResponse
import com.example.storyapp_kotlin.data.api.Story
import com.example.storyapp_kotlin.data.api.StoryResponse
import com.example.storyapp_kotlin.data.paging.dao.StoryDatabase
import com.example.storyapp_kotlin.data.paging.paging.StoryRemoteMediator
import com.example.storyapp_kotlin.data.paging.paging.StoryRemoteMediators
import com.example.storyapp_kotlin.data.pref.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject


class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    private val userPreference: UserPreference
) {

    private val storyDao = database.storyDao()

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }


    @OptIn(ExperimentalPagingApi::class)
    fun getStory(token: String,): Flow<PagingData<Story>> {
        val bearerToken = generateBearerToken(token)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, bearerToken),
            pagingSourceFactory = {
                storyDao.getAllStory()
            }
        ).flow
    }



    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.uploadImage(bearerToken, file, description)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }



    fun getStoriesWithLocation(
        token: String
    ): Flow<Result<MapResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getStoriesWithLocation(32, 1, bearerToken)
            if (!response.error) {
                emit(Result.success(response))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)



    suspend fun saveUserToken(token: String) {
        userPreference.saveUserToken(token)
    }

    fun getUserToken(): Flow<String?> = userPreference.getSessions()


}