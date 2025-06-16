package com.rafael.ibooks.data.remote.service

import com.rafael.ibooks.data.remote.model.BookItem
import com.rafael.ibooks.data.remote.model.BooksResponse
import com.rafael.ibooks.data.remote.utils.ApiConstants.LANG_RESTRICT
import com.rafael.ibooks.data.remote.utils.ApiConstants.PRINT_TYPE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoogleBooksApi {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("orderBy") orderBy: String? = null,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int,
        //@Query("langRestrict") lang: String = LANG_RESTRICT,
        @Query("printType") type: String = PRINT_TYPE
    ): BooksResponse

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): BookItem
}