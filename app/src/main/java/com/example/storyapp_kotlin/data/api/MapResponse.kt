package com.example.storyapp_kotlin.data.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryMapsItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
) : Parcelable

@Parcelize
data class ListStoryMapsItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Double
) : Parcelable