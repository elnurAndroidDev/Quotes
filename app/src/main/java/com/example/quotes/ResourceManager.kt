package com.example.quotes

import android.content.Context
import androidx.annotation.StringRes

interface ResourceManager {
    fun getString(@StringRes strId: Int): String
}

class BaseResourceManager(private val context: Context) : ResourceManager {
    override fun getString(@StringRes strId: Int) = context.getString(strId)
}