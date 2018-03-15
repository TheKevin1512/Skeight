package com.kevindom.skeight.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.adapter.viewholder.BaseViewHolder
import com.kevindom.skeight.databinding.ItemChatterBinding
import com.kevindom.skeight.model.Chatter
import com.kevindom.skeight.transform.RoundedCornersTransform
import com.squareup.picasso.Picasso

class ChatterAdapter(
        private val layoutInflater: LayoutInflater
) : BaseAdapter<Chatter, ChatterAdapter.ViewHolder>() {

    fun remove(userId: String) {
        val toRemove = items.find { it.userId == userId }
        val index = items.indexOf(toRemove)
        if (index > 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_chatter.bind(layoutInflater, parent))

    inner class ViewHolder(
            private val binding: ItemChatterBinding
    ) : BaseViewHolder<Chatter>(binding.root) {

        override fun bind(item: Chatter, position: Int) {
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