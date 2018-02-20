package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ItemChatterBinding
import com.kevindom.skeight.transform.RoundedCornersTransform
import com.squareup.picasso.Picasso

class ChatterAdapter(
        private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<ChatterAdapter.ViewHolder>() {

    //first: PhotoUrl, second: Name
    private val items: MutableList<Pair<String?, String>> = mutableListOf()

    fun add(item: Pair<String?, String>) {
        items.add(item)
        notifyItemInserted(itemCount - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_chatter.bind(layoutInflater, parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatterAdapter.ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(
            private val binding: ItemChatterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<String?, String>) {
            //Photo URL
            item.first?.let {
                Picasso.with(binding.root.context)
                        .load(item.first)
                        .transform(RoundedCornersTransform())
                        .into(binding.chatterPicture)
            }
            binding.chatterName.text = item.second
        }
    }
}