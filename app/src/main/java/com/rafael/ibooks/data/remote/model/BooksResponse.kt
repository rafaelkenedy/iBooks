package com.rafael.ibooks.data.remote.model

import com.google.gson.annotations.SerializedName


data class BooksResponse(
    @SerializedName("items")
    val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title")
    val title: String?,

    @SerializedName("authors")
    val authors: List<String>?,

    @SerializedName("publisher")
    val publisher: String?,

    @SerializedName("publishedDate")
    val publishedDate: String?,

    @SerializedName("pageCount")
    val pageCount: Int?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("imageLinks")
    val imageLinks: ImageLinks?
) {
    val publishedYear: Int?
        get() = try {
            publishedDate?.take(4)?.toInt()
        } catch (e: Exception) {
            null
        }

    val thumbnailSafe: String
        get() = imageLinks?.thumbnail
            ?: "https://placehold.co/128x188?text=&font=cabin"
}

data class ImageLinks(
    @SerializedName("thumbnail")
    val thumbnail: String?
)