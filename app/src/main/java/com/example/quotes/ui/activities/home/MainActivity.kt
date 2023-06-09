package com.example.quotes.ui.activities.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.quotes.R
import com.example.quotes.Resource
import com.example.quotes.adapters.QuotesAdapter
import com.example.quotes.databinding.ActivityMainBinding
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.repository.QuotesRepository
import com.example.quotes.ui.QuoteClickListener
import com.example.quotes.ui.QuotesViewModel
import com.example.quotes.ui.QuotesViewModelProviderFactory
import com.example.quotes.ui.Updater
import com.example.quotes.ui.activities.favorites.FavoritesActivity
import com.example.quotes.ui.activities.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: QuotesViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var quotesAdapter: QuotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = QuotesRepository(QuotesDatabase(this))
        val viewModelProviderFactory = QuotesViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[QuotesViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        setupViewPager()

        viewModel.quotes.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visibility = View.GONE
                    it.data?.let { response ->
                        quotesAdapter.differ.submitList(response)
                    }
                }

                is Resource.Error -> {
                    binding.progressCircle.visibility = View.GONE
                    it.data?.let { response ->
                        quotesAdapter.differ.submitList(response)
                    }
                }

                is Resource.Loading -> {
                    binding.progressCircle.visibility = View.VISIBLE
                }

                else -> {}
            }
        }

    }

    private fun setupViewPager() {
        val quoteClickListener = object : QuoteClickListener {
            override fun likeOrUnLike(quote: QuoteUiModel) {
                if (!quote.liked) {
                    viewModel.saveQuote(quote)
                } else {
                    viewModel.deleteQuote(quote)
                }
                quote.liked = !quote.liked
            }

            override fun share(quote: QuoteUiModel) {
                val text = "“${quote.content}”\n-${quote.author}"
                val link = "Developer contact:\nhttps://t.me/elnurIsaevBlog"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$text\n\n$link")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        val updater = object : Updater {
            override fun update() {
                viewModel.getQuotesList()
            }
        }

        quotesAdapter = QuotesAdapter(quoteClickListener, updater)
        binding.viewPager.adapter = quotesAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == quotesAdapter.differ.currentList.size - 1) {
                    viewModel.getQuotesList()
                }
                super.onPageSelected(position)
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> startActivity(Intent(this, FavoritesActivity::class.java))
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = this.getSharedPreferences("AppearancePreferences", Context.MODE_PRIVATE) ?: return
        val defaultValue = 0
        val textSize = sharedPref.getInt(getString(R.string.quote_size_key), defaultValue)
        Log.d("MyLog", "$textSize  resume")
        quotesAdapter.setTextSize(textSize)
    }
}