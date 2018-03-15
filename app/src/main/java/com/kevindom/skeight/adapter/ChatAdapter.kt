package com.kevindom.skeight.adapter

import android.databinding.ViewDataBinding
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import bind
import com.kevindom.skeight.BR
import com.kevindom.skeight.R
import com.kevindom.skeight.activity.FullScreenActivity
import com.kevindom.skeight.adapter.viewholder.BaseViewHolder
import com.kevindom.skeight.databinding.ItemMessageInfoBinding
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.transform.DimensionTransform
import com.squareup.picasso.Picasso
import loop

class ChatAdapter(
        private val layoutInflater: LayoutInflater,
        private val userId: String
) : BaseAdapter<Message, BaseViewHolder<Message>>() {

    private companion object {
        const val INFO_MESSAGE = -1
        const val MY_MESSAGE = 0
        const val OTHER_MESSAGE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position].userId.isBlank() -> INFO_MESSAGE
            items[position].userId == userId -> MY_MESSAGE
            else -> OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<Message> {
        return when (viewType) {
            MY_MESSAGE -> ChatMessageHolder(R.layout.item_message_self.bind(layoutInflater, parent))
            OTHER_MESSAGE -> ChatMessageHolder(R.layout.item_message.bind(layoutInflater, parent))
            else -> InfoMessageHolder(R.layout.item_message_info.bind(layoutInflater, parent))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Message>, position: Int) {
        when (holder) {
            is ChatMessageHolder -> holder.bind(items[position], position)
            is InfoMessageHolder -> holder.bind(items[position], position)
        }
    }

    inner class ChatMessageHolder(private val binding: ViewDataBinding) : BaseViewHolder<Message>(binding.root) {

        override fun bind(item: Message, position: Int) {
            val context = binding.root.context

            binding.setVariable(BR.message, item)
            binding.setVariable(BR.showName, !(position < items.size - 1 && items[position + 1].userId == item.userId))
            binding.setVariable(BR.hasPicture, false)

            item.photoUrl?.let { url ->
                binding.setVariable(BR.hasPicture, true)

                val chatPicture = binding.root.findViewById<ImageView>(R.id.chat_picture)
                chatPicture.setOnClickListener { context.startActivity(FullScreenActivity.create(context, url)) }

                val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_loading)
                drawable?.loop()
                Picasso.with(context)
                        .load(url)
                        .placeholder(drawable)
                        .transform(DimensionTransform(context))
                        .into(chatPicture)
            }
        }
    }

    inner class InfoMessageHolder(private val binding: ItemMessageInfoBinding) : BaseViewHolder<Message>(binding.root) {
        override fun bind(item: Message, position: Int) {
            binding.message = item.content
        }
    }
}