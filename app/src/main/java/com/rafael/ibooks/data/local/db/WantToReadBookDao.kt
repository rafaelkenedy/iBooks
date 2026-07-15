package com.rafael.ibooks.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WantToReadBookDao {

    @Query("SELECT * FROM want_to_read_books ORDER BY savedAt DESC")
    fun observeBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBook(book: BookEntity)

    @Query("DELETE FROM want_to_read_books WHERE id = :bookId")
    suspend fun removeBook(bookId: String)
}
