package com.kevindom.skeight.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseViewHolder<in T>(root: View) : RecyclerView.ViewHolder(root) {

    abstract fun bind(item: T, position: Int)
}