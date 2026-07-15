package com.rafael.ibooks.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BookConverters::class)
abstract class IBooksDatabase : RoomDatabase() {
    abstract fun wantToReadBookDao(): WantToReadBookDao
}
