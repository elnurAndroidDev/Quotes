package com.example.quotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quotes.models.FavoriteQuoteDBModel
import com.example.quotes.models.CachedQuoteDBModel


@Database(
    entities = [FavoriteQuoteDBModel::class, CachedQuoteDBModel::class],
    version = 1
)
abstract class QuotesDatabase : RoomDatabase() {

    abstract fun getQuoteDao(): QuoteDAO

    abstract fun getCachedQuoteDao(): CachedQuoteDao

    companion object {
        @Volatile
        private var instance: QuotesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                QuotesDatabase::class.java,
                "quotes_db.db"
            ).build()
    }
}