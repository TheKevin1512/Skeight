package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ItemRoomBinding
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.model.Room

class RoomAdapter(
        private val layoutInflater: LayoutInflater,
        private val roomManager: RoomManager,
        private val userId: String,
        private val clickListener: OnClickListener
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    private companion object {
        const val FIRST_ITEM_INDEX = 0
    }

    private val items: MutableList<Room> = mutableListOf()

    fun add(item: Room) {
        this.items.add(FIRST_ITEM_INDEX, item)
        notifyItemInserted(FIRST_ITEM_INDEX)
    }

    fun update(item: Room) {
        val toRemove = items.find { it.id == item.id }
        val index = items.indexOf(toRemove)
        if (index > -1) {
            items.removeAt(index)
            items.add(index, item)
            notifyItemChanged(index)
        }
    }

    fun remove(item: Room) {
        val index = items.indexOf(item)
        if (index > -1) {
            this.items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_room.bind(layoutInflater, parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun onViewRecycled(holder: ViewHolder) = holder.reset()

    inner class ViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.root.setOnClickListener { clickListener.onRoomClicked(room) }
            binding.root.setOnLongClickListener {
                binding.roomBtnRemove.visibility = if (binding.roomBtnRemove.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }
            binding.roomBtnRemove.setOnClickListener {
                remove(room)
                roomManager.removeUser(room.id, userId)
            }
            binding.roomName.text = room.name
            binding.roomRecent.text = room.lastMessage
            binding.executePendingBindings()
        }

        fun reset() {
            binding.roomBtnRemove.visibility = View.GONE
        }
    }

    interface OnClickListener {
        fun onRoomClicked(room: Room)
    }
}