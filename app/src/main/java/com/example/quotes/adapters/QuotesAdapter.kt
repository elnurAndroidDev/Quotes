package com.example.quotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.R
import com.example.quotes.models.Quote
import kotlinx.android.synthetic.main.item_quote.view.*

class QuotesAdapter : RecyclerView.Adapter<QuotesAdapter.QuotesViewHolder>() {

    inner class QuotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Quote>() {
        override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean =
            oldItem.content == newItem.content

        override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotesViewHolder {
        return QuotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_quote,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: QuotesViewHolder, position: Int) {
        val quote = differ.currentList[position]
        holder.itemView.apply {
            quoteTextView.text = quote.content
            authorTextView.text = quote.author
        }
    }
}