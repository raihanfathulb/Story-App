package com.example.storyapp_kotlin.utils

import com.example.storyapp_kotlin.data.api.Story

object DataDummy {

    fun generateDummyNewStories(): List<Story> {
        val storyList = ArrayList<Story>()
        for (i in 0..5) {
            val stories = Story(
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2026-06-06T06:16:26Z",
                "Yanto Karbs",
                "Testo",
                null,
                "id $i",
                null,
            )
            storyList.add(stories)
        }
        return storyList
    }

}