package com.kevindom.skeight.transform

import android.content.Context
import android.graphics.Bitmap
import com.squareup.picasso.Transformation


class DimensionTransform(
        context: Context
) : Transformation {

    private val targetWidth = context.resources.displayMetrics.widthPixels * 0.5

    override fun key(): String = "transformation: " + targetWidth.toString()

    override fun transform(source: Bitmap): Bitmap {
        val aspectRatio = source.height.toFloat() / source.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        val result = Bitmap.createScaledBitmap(source, targetWidth.toInt(), targetHeight, false)
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle()
        }
        return result
    }
}