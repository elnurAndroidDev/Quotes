package com.example.quotes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.quotes.models.QuoteDBModel
import com.example.quotes.models.QuoteDBModel2

@Dao
interface QuoteDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(quote: QuoteDBModel): Long

    @Query("SELECT * FROM quotes ORDER BY id DESC")
    fun getAllQuotes(): LiveData<List<QuoteDBModel>>

    @Query("DELETE FROM quotes WHERE content = :quoteContent")
    suspend fun delete(quoteContent: String)

}