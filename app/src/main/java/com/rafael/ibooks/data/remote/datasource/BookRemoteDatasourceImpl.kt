package com.rafael.ibooks.data.remote.datasource

import com.rafael.ibooks.data.remote.model.BooksResponse
import com.rafael.ibooks.data.remote.service.IGoogleBooksApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRemoteDatasourceImpl(
    private val googleBooksApi: IGoogleBooksApi
) : IBookRemoteDataSource {
    override suspend fun searchBooks(
        query: String,
        maxResults: Int,
        startIndex: Int
    ): BooksResponse {
        return withContext(Dispatchers.IO) {
            val response = googleBooksApi.searchBooks(query, maxResults, startIndex).execute()
            if (response.isSuccessful) {
                response.body() ?: BooksResponse(emptyList())
            } else {
                throw Exception("Erro na API: ${response.code()} - ${response.message()}")
            }
        }
    }

    override suspend fun getRecentBooks(): BooksResponse {
        return withContext(Dispatchers.IO) {
            val response = googleBooksApi.getRecentBooks().execute()
            if (response.isSuccessful) {
                response.body() ?: BooksResponse(emptyList())
            } else {
                throw Exception("Erro: ${response.code()} - ${response.message()}")
            }
        }
    }
}
