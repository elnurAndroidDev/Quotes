package com.example.quotes

import android.app.Application

class App : Application() {

    lateinit var viewModel: AppViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = AppViewModel(Model(BaseResourceManager(this)))
    }
}