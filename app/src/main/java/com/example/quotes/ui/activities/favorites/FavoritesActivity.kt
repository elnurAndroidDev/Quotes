package com.example.quotes.ui.activities.favorites

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.adapters.SavedQuotesAdapter
import com.example.quotes.databinding.ActivityFavoritesBinding
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.repository.QuotesRepository
import com.example.quotes.ui.QuoteClickListener

class FavoritesActivity : AppCompatActivity() {

    private lateinit var adapter: SavedQuotesAdapter
    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = QuotesRepository(QuotesDatabase(this))
        val viewModelProviderFactory = FavoritesViewModelFactory(newsRepository)
        val viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[FavoritesViewModel::class.java]

        adapter = SavedQuotesAdapter(object : QuoteClickListener {
            override fun likeOrUnLike(quote: QuoteUiModel) {
                val builder = AlertDialog.Builder(this@FavoritesActivity)
                builder.setTitle("Delete")
                builder.setMessage("Delete from your favorites?")
                builder.setPositiveButton("Yes") { dialogInterface, _ ->
                    viewModel.deleteQuote(quote)
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                builder.show()
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

            override fun translate(quoteContent: String) {

            }
        })
        binding.favRV.adapter = adapter
        binding.favRV.layoutManager = LinearLayoutManager(this)

        viewModel.getSavedQuotes().observe(this) {
            adapter.differ.submitList(it.map { qdb ->
                qdb.toUiModel()
            })
        }

    }
}