package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ItemChatterBinding
import com.kevindom.skeight.model.Chatter
import com.kevindom.skeight.transform.RoundedCornersTransform
import com.squareup.picasso.Picasso

class ChatterAdapter(
        private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<ChatterAdapter.ViewHolder>() {

    private val items: MutableList<Chatter> = mutableListOf()

    fun add(item: Chatter) {
        items.add(item)
        notifyItemInserted(itemCount - 1)
    }

    fun remove(userId: String) {
        val toRemove = items.find { it.userId == userId }
        val index = items.indexOf(toRemove)
        if (index > 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_chatter.bind(layoutInflater, parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatterAdapter.ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(
            private val binding: ItemChatterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Chatter) {
            //Photo URL
            item.photoUrl?.let {
                Picasso.with(binding.root.context)
                        .load(it)
                        .transform(RoundedCornersTransform())
                        .into(binding.chatterPicture)
            }
            binding.chatterName.text = item.name
        }
    }
}