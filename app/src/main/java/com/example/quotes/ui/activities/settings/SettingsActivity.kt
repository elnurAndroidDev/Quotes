package com.example.quotes.ui.activities.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quotes.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SettingsActivity : AppCompatActivity() {
    private lateinit var quoteTextView: TextView
    private lateinit var authorTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        quoteTextView = findViewById(R.id.demoQuote)
        authorTextView = findViewById(R.id.demoAuthor)
        showBottomSheetDialog()
    }


    private fun showBottomSheetDialog() {
        val sharedPref = this.getSharedPreferences("AppearancePreferences", Context.MODE_PRIVATE) ?: return
        val defaultValue = 0
        val textSize = sharedPref.getInt(getString(R.string.quote_size_key), defaultValue)

        quoteTextView.textSize = (textSize+24).toFloat()
        authorTextView.textSize = (textSize+16).toFloat()

        val bottomSheetLayout = findViewById<LinearLayout>(R.id.bottom_sheet_layout)
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        val headerTextView = findViewById<TextView>(R.id.bottom_sheet_header)
        val quoteSizeTextView = findViewById<TextView>(R.id.quoteSizeTextView)
        val t = (textSize+24).toString()
        quoteSizeTextView.text = t

        headerTextView.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        val quoteSizeSeekBar = findViewById<SeekBar>(R.id.quoteSizeSeekBar)
        quoteSizeSeekBar.progress = textSize
        quoteSizeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, size: Int, p2: Boolean) {
                quoteTextView.textSize = size.toFloat() + 24
                authorTextView.textSize = size.toFloat() + 16
                val text = (size+24).toString()
                quoteSizeTextView.text = text
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                with (sharedPref.edit()) {
                    putInt(getString(R.string.quote_size_key), p0?.progress ?: 0)
                    apply()
                }
            }

        })
    }
}