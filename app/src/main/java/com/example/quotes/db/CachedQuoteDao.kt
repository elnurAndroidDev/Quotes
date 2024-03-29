package com.example.quotes.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quotes.models.CachedQuoteDBModel


@Dao
interface CachedQuoteDao {

    @Query("SELECT COUNT(*) FROM cache_quotes")
    suspend fun getCachedQuotesNumber(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<CachedQuoteDBModel>)

    @Query("SELECT * FROM cache_quotes")
    suspend fun getFromCache(): List<CachedQuoteDBModel>
}