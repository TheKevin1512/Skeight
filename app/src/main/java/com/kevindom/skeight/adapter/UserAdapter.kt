package com.kevindom.skeight.adapter

import android.databinding.ObservableBoolean
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.adapter.viewholder.BaseViewHolder
import com.kevindom.skeight.databinding.ItemUserBinding
import com.kevindom.skeight.firebase.AnalyticsManager
import com.kevindom.skeight.model.User
import com.kevindom.skeight.transform.RoundedCornersTransform
import com.squareup.picasso.Picasso
import startAnimation

class UserAdapter(
        private val layoutInflater: LayoutInflater,
        private val analyticsManager: AnalyticsManager
) : BaseAdapter<Pair<User, ObservableBoolean>, UserAdapter.ViewHolder>() {

    val selectedUsers get() = items.filter { it.second.get() }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_user.bind(layoutInflater, parent))

    inner class ViewHolder(private val binding: ItemUserBinding) : BaseViewHolder<Pair<User, ObservableBoolean>>(binding.root) {

        override fun bind(item: Pair<User, ObservableBoolean>, position: Int) {
            binding.root.setOnClickListener {
                val selected = item.second.get()
                if (selected) {
                    binding.userAdded.startAnimation(R.drawable.anim_uncheck, reset = true)
                } else {
                    binding.userAdded.startAnimation(R.drawable.anim_check)
                }
                item.second.set(!selected)
                analyticsManager.logSelectItem(item.first, selected)
            }
            binding.user = item.first
            binding.executePendingBindings()
            item.first.photoUrl?.let {
                Picasso.with(binding.root.context)
                        .load(it)
                        .transform(RoundedCornersTransform())
                        .into(binding.userImage)
            }
        }
    }
}