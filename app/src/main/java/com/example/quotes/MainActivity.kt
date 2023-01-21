package com.example.quotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = (application as App).viewModel

        val quoteTextView = findViewById<TextView>(R.id.quoteTextView)
        val authorTextView = findViewById<TextView>(R.id.authorTextView)
        val button = findViewById<Button>(R.id.getQuoteButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.INVISIBLE

        button.setOnClickListener {
            quoteTextView.text = ""
            authorTextView.text = ""
            progressBar.visibility = View.VISIBLE
            button.isEnabled = false
            viewModel.getQuote()
        }

        viewModel.init(object : TextCallback {
            override fun provideText(quote: String, author: String) = runOnUiThread {
                quoteTextView.text = quote
                authorTextView.text = author
                progressBar.visibility = View.INVISIBLE
                button.isEnabled = true
            }
        })
    }

    override fun onDestroy() {
        viewModel.clear()
        super.onDestroy()
    }
}