package com.example.quotes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.R
import com.example.quotes.databinding.QuoteItemBinding
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.ui.QuoteClickListener

class QuotesAdapter(private val quoteClickListener: QuoteClickListener) :
    RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder>() {

    inner class QuoteViewHolder(val binding: QuoteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<QuoteUiModel>() {
        override fun areContentsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem.content == newItem.content

        override fun areItemsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val binding = QuoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuoteViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = differ.currentList[position]
        holder.itemView.apply {
            val t = "“${quote.content}”"
            holder.binding.quoteTextView.text = t
            holder.binding.authorTextView.text = quote.author
            if (quote.liked) {
                holder.binding.likeButton.setBackgroundResource(R.drawable.heart_filled)
            } else {
                holder.binding.likeButton.setBackgroundResource(R.drawable.heart)
            }
            holder.binding.likeButton.setOnClickListener {
                quoteClickListener.likeOrUnLike(quote)
                if (quote.liked) {
                    holder.binding.likeButton.setBackgroundResource(R.drawable.heart_filled)
                } else {
                    holder.binding.likeButton.setBackgroundResource(R.drawable.heart)
                }
            }
        }
    }
}