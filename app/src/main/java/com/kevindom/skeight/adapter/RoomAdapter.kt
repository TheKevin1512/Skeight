package com.kevindom.skeight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.adapter.viewholder.BaseViewHolder
import com.kevindom.skeight.databinding.ItemRoomBinding
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.model.Room

class RoomAdapter(
        private val layoutInflater: LayoutInflater,
        private val roomManager: RoomManager,
        private val userId: String,
        private val clickListener: (Room) -> Unit
) : BaseAdapter<Room, RoomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_room.bind(layoutInflater, parent))

    override fun onViewRecycled(holder: ViewHolder) = holder.reset()

    inner class ViewHolder(private val binding: ItemRoomBinding) : BaseViewHolder<Room>(binding.root) {

        override fun bind(item: Room) {
            binding.root.setOnClickListener {
                reset()
                clickListener(item)
            }
            binding.root.setOnLongClickListener {
                binding.roomBtnRemove.visibility = if (binding.roomBtnRemove.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }
            binding.roomBtnRemove.setOnClickListener {
                remove(item)
                roomManager.removeUser(item.id, userId)
            }
            binding.roomName.text = item.name
            binding.roomRecent.text = item.lastMessage
            binding.executePendingBindings()
        }

        fun reset() {
            if (binding.roomBtnRemove.visibility == View.VISIBLE) {
                binding.roomBtnRemove.visibility = View.GONE
            }
        }
    }
}