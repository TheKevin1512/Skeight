package com.kevindom.skeight.adapter

import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.activity.FullScreenActivity
import com.kevindom.skeight.databinding.ItemMessageBinding
import com.kevindom.skeight.databinding.ItemMessageSelfBinding
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.transform.DimensionTransform
import com.squareup.picasso.Picasso
import loop

class ChatAdapter(
        private val layoutInflater: LayoutInflater,
        private val userId: String
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private companion object {
        const val NEW_MESSAGE_INDEX = 0

        const val MY_MESSAGE = 0
        const val OTHER_MESSAGE = 1
    }

    private val items: MutableList<Message> = mutableListOf()

    fun update(item: Message) {
        this.items.add(NEW_MESSAGE_INDEX, item)
        notifyItemInserted(NEW_MESSAGE_INDEX)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].userId == userId)
            MY_MESSAGE
        else
            OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return if (viewType == MY_MESSAGE) {
            ViewHolder(myChatBinding = R.layout.item_message_self.bind(layoutInflater, parent))
        } else {
            ViewHolder(otherChatBinding = R.layout.item_message.bind(layoutInflater, parent))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)

    inner class ViewHolder : RecyclerView.ViewHolder {
        private var myChatBinding: ItemMessageSelfBinding? = null
        private var otherChatBinding: ItemMessageBinding? = null

        constructor(otherChatBinding: ItemMessageBinding) : super(otherChatBinding.root) {
            this.otherChatBinding = otherChatBinding
        }

        constructor(myChatBinding: ItemMessageSelfBinding) : super(myChatBinding.root) {
            this.myChatBinding = myChatBinding
        }

        fun bind(message: Message, position: Int) {
            myChatBinding?.let { binding ->
                val context = binding.root.context
                binding.message = message
                binding.showName = !(position < items.size - 1 && items[position + 1].userId == message.userId)
                binding.hasPicture = false
                message.photoUrl?.let { url ->
                    binding.chatPicture.setOnClickListener { context.startActivity(FullScreenActivity.create(context, url)) }
                    binding.hasPicture = true

                    val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_loading)
                    drawable?.loop()
                    Picasso.with(context)
                            .load(url)
                            .placeholder(drawable)
                            .transform(DimensionTransform(context))
                            .into(binding.chatPicture)
                }
            }
            otherChatBinding?.let { binding ->
                val context = binding.root.context
                binding.message = message
                binding.showName = !(position < items.size - 1 && items[position + 1].userId == message.userId)
                binding.hasPicture = false
                message.photoUrl?.let { url ->
                    binding.chatPicture.setOnClickListener { context.startActivity(FullScreenActivity.create(context, url)) }
                    binding.hasPicture = true

                    val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_loading)
                    drawable?.loop()
                    Picasso.with(context)
                            .load(url)
                            .placeholder(drawable)
                            .transform(DimensionTransform(context))
                            .into(binding.chatPicture)
                }
            }
        }
    }
}