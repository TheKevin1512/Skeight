package com.kevindom.skeight.adapter

import android.support.v7.widget.RecyclerView
import com.kevindom.skeight.adapter.viewholder.BaseViewHolder

abstract class BaseAdapter<T, V : BaseViewHolder<T>> : RecyclerView.Adapter<V>() {

    private companion object {
        const val FIRST_INDEX = 0
    }

    protected val items: MutableList<T> = mutableListOf()

    fun add(item: T, first: Boolean = false) {
        if (first) {
            items.add(FIRST_INDEX, item)
            notifyItemInserted(FIRST_INDEX)
        } else {
            items.add(item)
            notifyItemInserted(itemCount - 1)
        }
    }

    fun remove(item: T) {
        val index = items.indexOf(item)
        if (index > -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun update(item: T) {
        val index = items.indexOf(item)
        if (index > -1) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    fun updateAll(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: V, position: Int) = holder.bind(items[position])
}