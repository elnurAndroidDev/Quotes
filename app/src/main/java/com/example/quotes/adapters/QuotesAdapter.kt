package com.example.quotes.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.quotes.R
import com.example.quotes.databinding.ErrorItemBinding
import com.example.quotes.databinding.QuoteItemBinding
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.ui.QuoteClickListener
import com.example.quotes.ui.Updater

class QuotesAdapter(
    private val quoteClickListener: QuoteClickListener,
    private val updater: Updater
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_QUOTE = 0
    private val VIEW_TYPE_ERROR = 1

    private var textSize = 32
    private var font: Typeface? = null
    private var textColor = Color.BLACK

    fun setPreferences(font: Typeface, textSize: Int, textColor: Int) {
        this.font = font
        this.textSize = textSize
        this.textColor = textColor
        notifyDataSetChanged()
    }

    abstract class ItemViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(quote: QuoteUiModel)
    }

    inner class QuoteViewHolder(private val binding: QuoteItemBinding) :
        ItemViewHolder(binding) {
        override fun bind(quote: QuoteUiModel) {
            binding.quoteTextView.textSize = textSize.toFloat() + 24
            binding.authorTextView.textSize = textSize.toFloat() + 16
            binding.quoteTextView.typeface = font
            binding.authorTextView.typeface = font
            binding.quoteTextView.setTextColor(textColor)
            binding.authorTextView.setTextColor(textColor)
            val content = "“${quote.content}”"
            binding.quoteTextView.text = content
            val author = "-${quote.author}"
            binding.authorTextView.text = author
            if (quote.liked) {
                binding.likeButton.setBackgroundResource(R.drawable.heart_filled)
            } else {
                binding.likeButton.setBackgroundResource(R.drawable.heart)
            }
            binding.likeButton.setOnClickListener {
                quoteClickListener.likeOrUnLike(quote)
                if (quote.liked) {
                    binding.likeButton.setBackgroundResource(R.drawable.heart_filled)
                } else {
                    binding.likeButton.setBackgroundResource(R.drawable.heart)
                }
            }
            binding.shareButton.setOnClickListener {
                quoteClickListener.share(quote)
            }
        }
    }

    inner class ErrorViewHolder(private val binding: ErrorItemBinding) :
        ItemViewHolder(binding) {
        override fun bind(quote: QuoteUiModel) {
            binding.errorTextView.textSize = textSize.toFloat() + 24
            binding.errorAuthorTextView.textSize = textSize.toFloat() + 16
            binding.errorTextView.typeface = font
            binding.errorAuthorTextView.typeface = font
            binding.errorTextView.setTextColor(textColor)
            binding.errorAuthorTextView.setTextColor(textColor)
            val content = "“${quote.content}”"
            binding.errorTextView.text = content
            val author = "-${quote.author}"
            binding.errorAuthorTextView.text = author
            binding.updateButton.setOnClickListener {
                updater.update()
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<QuoteUiModel>() {
        override fun areContentsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem.content == newItem.content

        override fun areItemsTheSame(oldItem: QuoteUiModel, newItem: QuoteUiModel): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return if (viewType == VIEW_TYPE_QUOTE) {
            val binding =
                QuoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            QuoteViewHolder(binding)
        } else {
            val binding =
                ErrorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ErrorViewHolder(binding)
        }
    }

    override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        val quote = differ.currentList[position]
        return if (quote.author == "App")
            VIEW_TYPE_ERROR
        else VIEW_TYPE_QUOTE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quote = differ.currentList[position]
        when (holder) {
            is QuoteViewHolder -> holder.bind(quote)
            is ErrorViewHolder -> holder.bind(quote)
        }
    }
}