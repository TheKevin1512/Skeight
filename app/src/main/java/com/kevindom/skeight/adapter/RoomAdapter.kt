package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bind
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ItemRoomBinding
import com.kevindom.skeight.model.Room

class RoomAdapter(
        private val layoutInflater: LayoutInflater,
        private val listener: OnClickListener
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
        items.removeAt(index)
        items.add(index, item)
        notifyItemChanged(index)
    }

    fun remove(item: Room) {
        val index = this.items.indexOf(item)
        this.items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(R.layout.item_room.bind(layoutInflater, parent))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.root.setOnClickListener { listener.onRoomClicked(room) }
            binding.roomName.text = room.name
            binding.roomRecent.text = room.lastMessage
            binding.executePendingBindings()
        }
    }

    interface OnClickListener {
        fun onRoomClicked(room: Room)
    }
}