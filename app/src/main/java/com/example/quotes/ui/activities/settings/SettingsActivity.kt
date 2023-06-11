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
import com.example.quotes.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import top.defaults.colorpicker.ColorPickerView

class SettingsActivity : AppCompatActivity() {
    private lateinit var quoteTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var quoteSizeTextView: TextView
    private lateinit var quoteSizeSeekBar: SeekBar
    private lateinit var fontSpinner: Spinner
    private lateinit var colorIndicator: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        quoteTextView = findViewById(R.id.demoQuote)
        authorTextView = findViewById(R.id.demoAuthor)
        quoteSizeTextView = findViewById(R.id.quoteSizeTextView)
        quoteSizeSeekBar = findViewById(R.id.quoteSizeSeekBar)
        fontSpinner = findViewById(R.id.fontSpinner)
        colorIndicator = findViewById(R.id.quoteColorIndicator)
        settingViewsFromSharedPreferences()
        showBottomSheetDialog()
    }

    private fun settingViewsFromSharedPreferences() {
        val sharedPref =
            this.getSharedPreferences("AppearancePreferences", Context.MODE_PRIVATE) ?: return
        val textSize = sharedPref.getInt(getString(R.string.quote_size_key), 0)
        quoteTextView.textSize = (textSize + 24).toFloat()
        authorTextView.textSize = (textSize + 16).toFloat()
        val t = (textSize + 24).toString()
        quoteSizeTextView.text = t
        quoteSizeSeekBar.progress = textSize

        val fontPosition = sharedPref.getInt(getString(R.string.font_key), 0)
        fontSpinner.setSelection(fontPosition)

        val color = sharedPref.getInt(getString(R.string.quote_color_key), Color.BLACK)
        colorIndicator.setBackgroundColor(color)
        quoteTextView.setTextColor(color)
        authorTextView.setTextColor(color)
    }


    private fun showBottomSheetDialog() {

        val bottomSheetLayout = findViewById<LinearLayout>(R.id.bottom_sheet_layout)
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val headerTextView = findViewById<TextView>(R.id.bottom_sheet_header)
        headerTextView.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        quoteSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, size: Int, p2: Boolean) {
                quoteTextView.textSize = size.toFloat() + 24
                authorTextView.textSize = size.toFloat() + 16
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

        colorIndicator.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose text color")
            val dialogLayout = layoutInflater.inflate(R.layout.color_picker_layout, null)
            val colorPicker = dialogLayout.findViewById<ColorPickerView>(R.id.color_picker)
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                quoteTextView.setTextColor(colorPicker.color)
                authorTextView.setTextColor(colorPicker.color)
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
        quoteTextView.typeface = typeface
        authorTextView.typeface = typeface
    }
}