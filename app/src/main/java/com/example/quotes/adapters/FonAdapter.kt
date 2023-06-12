package com.example.quotes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quotes.databinding.FonItemBinding
import com.example.quotes.models.FonItem

class FonAdapter(private val fons: ArrayList<FonItem>, private val onFonClick: (Int) -> Unit) :
    RecyclerView.Adapter<FonAdapter.FonHolder>() {

    inner class FonHolder(private val binding: FonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fon: FonItem) {
            binding.fonItemTextView.text = fon.title
            binding.fonItemImageView.setBackgroundResource(fon.fonId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FonHolder {
        val binding =
            FonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FonHolder(binding)
    }

    override fun getItemCount() = fons.size


    override fun onBindViewHolder(holder: FonHolder, position: Int) {
        val fon = fons[position]
        holder.bind(fon)
        holder.itemView.setOnClickListener {
            onFonClick(fon.fonId)
        }
    }
}