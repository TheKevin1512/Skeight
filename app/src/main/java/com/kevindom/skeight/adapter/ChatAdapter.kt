package com.kevindom.skeight.adapter

import android.databinding.ViewDataBinding
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import bind
import com.kevindom.skeight.BR
import com.kevindom.skeight.R
import com.kevindom.skeight.activity.FullScreenActivity
import com.kevindom.skeight.databinding.ItemMessageInfoBinding
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.transform.DimensionTransform
import com.squareup.picasso.Picasso
import loop

class ChatAdapter(
        private val layoutInflater: LayoutInflater,
        private val userId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val NEW_MESSAGE_INDEX = 0

        const val INFO_MESSAGE = -1
        const val MY_MESSAGE = 0
        const val OTHER_MESSAGE = 1
    }

    private val items: MutableList<Message> = mutableListOf()

    fun update(item: Message) {
        this.items.add(NEW_MESSAGE_INDEX, item)
        notifyItemInserted(NEW_MESSAGE_INDEX)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position].userId.isBlank() -> INFO_MESSAGE
            items[position].userId == userId -> MY_MESSAGE
            else -> OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> ChatMessageHolder(R.layout.item_message_self.bind(layoutInflater, parent))
            OTHER_MESSAGE -> ChatMessageHolder(R.layout.item_message.bind(layoutInflater, parent))
            else -> InfoMessageHolder(R.layout.item_message_info.bind(layoutInflater, parent))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatMessageHolder -> holder.bind(items[position], position)
            is InfoMessageHolder -> holder.bind(items[position].content)
        }
    }

    inner class ChatMessageHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message, position: Int) {
            val context = binding.root.context

            binding.setVariable(BR.message, message)
            binding.setVariable(BR.showName, !(position < items.size - 1 && items[position + 1].userId == message.userId))
            binding.setVariable(BR.hasPicture, false)

            message.photoUrl?.let { url ->
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

    inner class InfoMessageHolder(private val binding: ItemMessageInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(infoMessage: String) {
            binding.message = infoMessage
        }
    }
}