package com.example.quotes.ui.activities.settings

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.R
import com.example.quotes.adapters.FonAdapter
import com.example.quotes.databinding.ActivitySettingsBinding
import com.example.quotes.models.FonItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import top.defaults.colorpicker.ColorPickerView

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var quoteSizeTextView: TextView
    private lateinit var quoteSizeSeekBar: SeekBar
    private lateinit var fontSpinner: Spinner
    private lateinit var colorIndicator: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomSheetViews()
        setupViewsFromSharedPreferences()
        showBottomSheetDialog()
    }

    private fun setupViewsFromSharedPreferences() {
        val sharedPref =
            this.getSharedPreferences("AppearancePreferences", Context.MODE_PRIVATE) ?: return
        val textSize = sharedPref.getInt(getString(R.string.quote_size_key), 0)
        binding.demoQuote.textSize = (textSize + 24).toFloat()
        binding.demoAuthor.textSize = (textSize + 16).toFloat()
        val t = (textSize + 24).toString()
        quoteSizeTextView.text = t
        quoteSizeSeekBar.progress = textSize

        val fontPosition = sharedPref.getInt(getString(R.string.font_key), 0)
        fontSpinner.setSelection(fontPosition)

        val color = sharedPref.getInt(getString(R.string.quote_color_key), Color.BLACK)
        colorIndicator.setBackgroundColor(color)
        binding.demoQuote.setTextColor(color)
        binding.demoAuthor.setTextColor(color)

        val fonId = sharedPref.getInt(getString(R.string.background_key), R.drawable.gradient)
        binding.fonContainer.setBackgroundResource(fonId)
    }

    private fun setupBottomSheetViews() {
        setupRecyclerView()
        setupQuoteSizeSeekBar()
        setupFontSpinner()
        setupColorIndicator()
    }


    private fun showBottomSheetDialog() {
        val bottomSheetLayout = findViewById<LinearLayout>(R.id.bottom_sheet_layout)
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        val headerTextView = findViewById<TextView>(R.id.bottom_sheet_header)
        headerTextView.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        binding.fonContainer.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupRecyclerView() {
        val fonItems = arrayListOf(
            FonItem("Dynamic Background", R.drawable.gradient),
            FonItem("Star Sky", R.drawable.fon1),
            FonItem("Snow", R.drawable.fon2),
        )
        val fonsRecyclerView = findViewById<RecyclerView>(R.id.fonsRecyclerView)
        fonsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = FonAdapter(fonItems) { fonId ->
                binding.fonContainer.setBackgroundResource(fonId)
                val sharedPref = this@SettingsActivity.getSharedPreferences(
                    "AppearancePreferences",
                    Context.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putInt(getString(R.string.background_key), fonId)
                    apply()
                }
            }
        }
    }

    private fun setupQuoteSizeSeekBar() {
        quoteSizeTextView = findViewById(R.id.quoteSizeTextView)
        quoteSizeSeekBar = findViewById(R.id.quoteSizeSeekBar)
        quoteSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, size: Int, p2: Boolean) {
                binding.demoQuote.textSize = size.toFloat() + 24
                binding.demoAuthor.textSize = size.toFloat() + 16
                val text = (size + 24).toString()
                quoteSizeTextView.text = text
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val sharedPref = this@SettingsActivity.getSharedPreferences(
                    "AppearancePreferences",
                    Context.MODE_PRIVATE
                ) ?: return
                with(sharedPref.edit()) {
                    putInt(getString(R.string.quote_size_key), p0?.progress ?: 0)
                    apply()
                }
            }

        })
    }

    private fun setupFontSpinner() {
        fontSpinner = findViewById(R.id.fontSpinner)
        fontSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                setFont(position)
                val sharedPref = this@SettingsActivity.getSharedPreferences(
                    "AppearancePreferences",
                    Context.MODE_PRIVATE
                ) ?: return
                with(sharedPref.edit()) {
                    putInt(getString(R.string.font_key), position)
                    apply()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun setupColorIndicator() {
        colorIndicator = findViewById(R.id.quoteColorIndicator)
        colorIndicator.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose text color")
            val dialogLayout = layoutInflater.inflate(R.layout.color_picker_layout, null)
            val colorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.color_picker)
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                binding.demoQuote.setTextColor(colorPicker.color)
                binding.demoAuthor.setTextColor(colorPicker.color)
                colorIndicator.setBackgroundColor(colorPicker.color)
                val sharedPref = this@SettingsActivity.getSharedPreferences(
                    "AppearancePreferences",
                    Context.MODE_PRIVATE
                )
                with(sharedPref.edit()) {
                    putInt(getString(R.string.quote_color_key), colorPicker.color)
                    apply()
                }
                dialogInterface.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.show()
        }
    }

    private fun setFont(position: Int) {
        val typeface = when (position) {
            0 -> ResourcesCompat.getFont(this, R.font.satoshi)
            1 -> ResourcesCompat.getFont(this, R.font.geosans_light)
            2 -> ResourcesCompat.getFont(this, R.font.pobeda)
            3 -> ResourcesCompat.getFont(this, R.font.president)
            4 -> ResourcesCompat.getFont(this, R.font.comic)
            else -> ResourcesCompat.getFont(this, R.font.satoshi)
        }
        binding.demoQuote.typeface = typeface
        binding.demoAuthor.typeface = typeface
    }
}