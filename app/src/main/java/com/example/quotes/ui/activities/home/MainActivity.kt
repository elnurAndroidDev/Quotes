package com.example.quotes.ui.activities.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: QuotesViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var bgColors: ArrayList<Int>
    private lateinit var sendBottomSheetDialog: BottomSheetDialog
    private lateinit var translationDialog: BottomSheetDialog
    private var translatedTextView: TextView? = null
    private var translationProgress: ProgressBar? = null
    private var copyTranslatedButton: LinearLayout? = null
    private var dynamicBackground = false
    private var quoteToSend: QuoteUiModel? = null
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
        setupSendDialog()
        setupTranslationDialog()

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

        viewModel.translation.observe(this) {
            when (it) {
                is Resource.Success, is Resource.Error -> {
                    translatedTextView?.visibility = View.VISIBLE
                    copyTranslatedButton?.visibility = View.VISIBLE
                    translationProgress?.visibility = View.GONE
                    it.data?.let { translatedTextView?.text = it }
                }
                is Resource.Loading -> {
                    translationDialog.show()
                    translatedTextView?.visibility = View.GONE
                    copyTranslatedButton?.visibility = View.GONE
                    translationProgress?.visibility = View.VISIBLE
                }
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
                quoteToSend = quote
                quotesAdapter.prepareItemForScreenshot()
                sendBottomSheetDialog.show()
            }

            override fun translate(quoteContent: String) {
                viewModel.translate(quoteContent)
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
                if (dynamicBackground) {
                    changeBackgroundColor()
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
        binding.progressCircle.indeterminateTintList = ColorStateList.valueOf(color)
        val fonId = sharedPref.getInt(getString(R.string.background_key), R.drawable.gradient)
        if (fonId == R.drawable.gradient) {
            dynamicBackground = true
            changeBackgroundColor()
        } else {
            dynamicBackground = false
            binding.viewPager.setBackgroundResource(fonId)
        }
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

    private fun setupTranslationDialog() {
        translationDialog = BottomSheetDialog(this)
        translationDialog.setContentView(R.layout.translation_bottom_sheet)
        translatedTextView = translationDialog.findViewById(R.id.translatedTextView)
        translationProgress = translationDialog.findViewById(R.id.translationProgressBar)
        copyTranslatedButton = translationDialog.findViewById(R.id.copyTranslationLayout)
        copyTranslatedButton?.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", translatedTextView?.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSendDialog() {
        sendBottomSheetDialog = BottomSheetDialog(this)
        sendBottomSheetDialog.setContentView(R.layout.send_bottom_sheet)
        val instaStories = sendBottomSheetDialog.findViewById<ImageView>(R.id.instagramStory)
        val saveImage = sendBottomSheetDialog.findViewById<ImageView>(R.id.saveImage)
        val copyText = sendBottomSheetDialog.findViewById<ImageView>(R.id.copyText)
        val sendText = sendBottomSheetDialog.findViewById<ImageView>(R.id.sendText)

        sendBottomSheetDialog.setOnCancelListener {
            quotesAdapter.backItemToDefault()
        }

        copyText?.setOnClickListener {
            val textToCopy = prepareTextToSendCopy()
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()
            sendBottomSheetDialog.cancel()
        }

        sendText?.setOnClickListener {
            sendBottomSheetDialog.cancel()
            val textToSend = prepareTextToSendCopy()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToSend)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        saveImage?.setOnClickListener {
            val screenshotBitmap = takeScreenshotOfView(binding.viewPager)
            saveImageToGallery(screenshotBitmap)
            sendBottomSheetDialog.cancel()
        }

        instaStories?.setOnClickListener {
            sendBottomSheetDialog.cancel()
            val screenshot = takeScreenshotOfView(binding.viewPager)
            val uri = saveImageToCacheAndGetUri(screenshot)
            val storiesIntent = Intent("com.instagram.share.ADD_TO_STORY")
            storiesIntent.setDataAndType(uri, "image/*")
            storiesIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            storiesIntent.setPackage("com.instagram.android")

            this.grantUriPermission(
                "com.instagram.android", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            try {
                startActivity(storiesIntent)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Instagram not found", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImageInQ(bitmap)
        else saveImageInLegacy(bitmap)
    }

    private fun saveImageInQ(bitmap: Bitmap) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream?
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        val contentResolver = application.contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }

        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)
    }

    private fun saveImageInLegacy(bitmap: Bitmap) {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "IMG_${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(image)
        fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    }

    private fun saveImageToCacheAndGetUri(bitmap: Bitmap): Uri? {
        val imagesFolder = File(this.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "screenshot.jpg")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                this.applicationContext,
                "com.example.quotes" + ".provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }

    private fun prepareTextToSendCopy(): String {
        val text = "“${quoteToSend?.content}”\n-${quoteToSend?.author}"
        val link = "Developer contact:\nhttps://t.me/elnurIsaevBlog"
        return "$text\n\n$link"
    }
}