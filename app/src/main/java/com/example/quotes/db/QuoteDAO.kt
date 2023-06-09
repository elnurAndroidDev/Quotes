package com.example.quotes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quotes.models.FavoriteQuoteDBModel

@Dao
interface QuoteDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(quote: FavoriteQuoteDBModel): Long

    @Query("SELECT * FROM quotes ORDER BY id DESC")
    fun getAllQuotes(): LiveData<List<FavoriteQuoteDBModel>>

    @Query("DELETE FROM quotes WHERE content = :quoteContent")
    suspend fun delete(quoteContent: String)

}