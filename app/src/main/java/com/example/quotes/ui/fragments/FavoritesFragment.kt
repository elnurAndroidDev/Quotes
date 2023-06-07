package com.example.quotes.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quotes.R
import com.example.quotes.adapters.SavedQuotesAdapter
import com.example.quotes.databinding.FragmentFavoritesBinding
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.ui.QuoteClickListener
import com.example.quotes.ui.activities.MainActivity

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private lateinit var adapter: SavedQuotesAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = (activity as MainActivity).viewModel

        adapter = SavedQuotesAdapter(object : QuoteClickListener {
            override fun likeOrUnLike(quote: QuoteUiModel) {
                val builder = AlertDialog.Builder(context)
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
        })
        binding.favRV.adapter = adapter
        binding.favRV.layoutManager = LinearLayoutManager(activity)

        viewModel.getSavedQuotes().observe(viewLifecycleOwner) {
            adapter.differ.submitList(it.map { qdb ->
                qdb.toUiModel()
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}