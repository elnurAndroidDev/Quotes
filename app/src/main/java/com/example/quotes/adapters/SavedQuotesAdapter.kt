package com.example.quotes.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.databinding.SavedQuoteItemBinding
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.ui.QuoteClickListener
import com.example.quotes.ui.activities.QuoteActivity

class SavedQuotesAdapter(private val quoteClickListener: QuoteClickListener) :
    RecyclerView.Adapter<SavedQuotesAdapter.SQViewHolder>() {

    inner class SQViewHolder(val binding: SavedQuoteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<QuoteUiModel>() {
        override fun areContentsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem.content == newItem.content

        override fun areItemsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SQViewHolder {
        val binding =
            SavedQuoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SQViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: SQViewHolder, position: Int) {
        val quote = differ.currentList[position]
        holder.itemView.apply {
            val c = quote.content
            val a = "— ${quote.author}"
            holder.binding.sqTextView.text = c
            holder.binding.sqAuthorTextView.text = a
            holder.binding.sqUnlikeButton.setOnClickListener {
                quoteClickListener.likeOrUnLike(quote)
            }
            setOnClickListener {
                context.startActivity(Intent(context, QuoteActivity::class.java))
            }
        }
    }
}