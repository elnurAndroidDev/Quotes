package com.example.quotes.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quotes.R
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.repository.QuotesRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: QuotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = QuotesRepository(QuotesDatabase(this))
        val viewModelProviderFactory = QuotesViewModelProviderFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[QuotesViewModel::class.java]

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val navController = findNavController(R.id.fragmentContainerView)
        bottomNavView.setupWithNavController(navController)
    }
}