package com.example.quotes.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.quotes.adapters.ViewPagerAdapter
import com.example.quotes.databinding.ActivityMainBinding
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.repository.QuotesRepository
import com.example.quotes.ui.QuotesViewModel
import com.example.quotes.ui.QuotesViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: QuotesViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = QuotesRepository(QuotesDatabase(this))
        val viewModelProviderFactory = QuotesViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[QuotesViewModel::class.java]

        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.navBar.itemActiveIndex = position
                super.onPageSelected(position)
            }
        })
        binding.navBar.onItemSelected = {
            binding.viewPager.setCurrentItem(it, false)
        }
    }
}