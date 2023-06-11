package com.example.quotes.ui.activities.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: QuotesViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var bgColors: ArrayList<Int>
    private var lastColorId = -1

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

        bgColors = ArrayList()
        bgColors.apply {
            add(ContextCompat.getColor(this@MainActivity, R.color.color1))
            add(ContextCompat.getColor(this@MainActivity, R.color.color2))
            add(ContextCompat.getColor(this@MainActivity, R.color.color3))
            add(ContextCompat.getColor(this@MainActivity, R.color.color4))
            add(ContextCompat.getColor(this@MainActivity, R.color.color5))
            add(ContextCompat.getColor(this@MainActivity, R.color.color6))
            add(ContextCompat.getColor(this@MainActivity, R.color.color7))
            add(ContextCompat.getColor(this@MainActivity, R.color.color8))
            add(ContextCompat.getColor(this@MainActivity, R.color.color9))
            add(ContextCompat.getColor(this@MainActivity, R.color.color10))
            add(ContextCompat.getColor(this@MainActivity, R.color.color11))
        }

        lastColorId = (0..10).random()
        binding.viewPager.setBackgroundColor(bgColors[lastColorId])
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
                val screenShot = takeScreenshotOfView(binding.mainContainer)
                val bytes = ByteArrayOutputStream()
                screenShot.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path =
                    MediaStore.Images.Media.insertImage(contentResolver, screenShot, "File", null)
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
                Log.d("MyLog", "pageChanged")
                changeBackgroundColor()
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
        settingViewsFromSharedPreferences()
    }

    private fun settingViewsFromSharedPreferences() {
        val sharedPref =
            this.getSharedPreferences("AppearancePreferences", Context.MODE_PRIVATE)
        val defaultValue = 0
        val textSize = sharedPref.getInt(getString(R.string.quote_size_key), defaultValue)
        val fontPosition = sharedPref.getInt(getString(R.string.font_key), 0)
        val color = sharedPref.getInt(getString(R.string.quote_color_key), Color.BLACK)
        quotesAdapter.setPreferences(
            font = getFontByPosition(fontPosition),
            textSize = textSize,
            textColor = color
        )

    }

    private fun getFontByPosition(pos: Int): Typeface {
        return when (pos) {
            0 -> ResourcesCompat.getFont(this, R.font.satoshi)!!
            1 -> ResourcesCompat.getFont(this, R.font.geosans_light)!!
            2 -> ResourcesCompat.getFont(this, R.font.pobeda)!!
            3 -> ResourcesCompat.getFont(this, R.font.president)!!
            4 -> ResourcesCompat.getFont(this, R.font.comic)!!
            else -> ResourcesCompat.getFont(this, R.font.satoshi)!!
        }
    }

    private fun changeBackgroundColor() {
        var randomColorId = (0..10).random()
        while (lastColorId == randomColorId) {
            randomColorId = (0..10).random()
        }
        val tColors =
            arrayOf(ColorDrawable(bgColors[lastColorId]), ColorDrawable(bgColors[randomColorId]))
        val transition = TransitionDrawable(tColors)
        binding.viewPager.background = transition
        transition.startTransition(500)
        lastColorId = randomColorId
    }

    private fun takeScreenshotOfView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }
}