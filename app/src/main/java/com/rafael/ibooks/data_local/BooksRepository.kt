package com.rafael.ibooks.data_local

import com.rafael.ibooks.domain.Book

object BooksRepository {
    fun getBooks(): List<Book> {
        val books = mutableListOf<Book>()
        repeat(10) {
            books.add(
                Book(
                    id = "",
                    title = "Harry Potter and the Sorcerer's Stone",
                    author = "J.K. Rowling",
                    pageCount = 320,
                    publisher = "Bloomsbury",
                    publishedYear = 1997,
                    imageUrl = "http://books.google.com/books/content?id=J_Q5EAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
                    description = "",
                    rating = 4.2,
                    ratingCount = 5,
                )
            )
        }
        return books
    }
}
