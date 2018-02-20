package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ItemUserBinding
import com.kevindom.skeight.firebase.AnalyticsManager
import com.kevindom.skeight.model.User
import com.kevindom.skeight.transform.RoundedCornersTransform
import com.squareup.picasso.Picasso
import startAnimation

class UserAdapter(
        private val layoutInflater: LayoutInflater,
        private val analyticsManager: AnalyticsManager
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val items: MutableList<User> = mutableListOf()
    val selectedItems: MutableMap<String, Boolean> = mutableMapOf()

    fun update(items: List<User>) {
        this.items.removeAll(this.items)
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_user.bind(layoutInflater, parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.root.setOnClickListener {
                if (selectedItems.containsKey(user.id)) {
                    selectedItems.remove(user.id)
                    binding.userAdded.startAnimation(R.drawable.anim_uncheck, reset = true)
                    analyticsManager.logSelectItem(user, false)
                } else {
                    selectedItems[user.id] = true
                    binding.userAdded.startAnimation(R.drawable.anim_check)
                    analyticsManager.logSelectItem(user, true)
                }
            }
            binding.user = user
            binding.executePendingBindings()
            user.photoUrl?.let {
                Picasso.with(binding.root.context)
                        .load(it)
                        .transform(RoundedCornersTransform())
                        .into(binding.userImage)
            }
        }
    }
}