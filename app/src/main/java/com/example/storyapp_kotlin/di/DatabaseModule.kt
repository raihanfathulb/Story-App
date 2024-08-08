package com.example.storyapp_kotlin.di

import android.content.Context
import androidx.room.Room
import com.example.storyapp_kotlin.data.paging.dao.RemoteKeysDao
import com.example.storyapp_kotlin.data.paging.dao.StoryDao
import com.example.storyapp_kotlin.data.paging.dao.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): StoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java, "story_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideStoryDao(database: StoryDatabase): StoryDao {
        return database.storyDao()
    }

    @Singleton
    @Provides
    fun provideRemoteKeysDao(database: StoryDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}