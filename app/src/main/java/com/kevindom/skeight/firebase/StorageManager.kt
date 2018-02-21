package com.kevindom.skeight.firebase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import com.kevindom.skeight.util.ImageHelper
import java.io.ByteArrayOutputStream

class StorageManager {

    private companion object {
        const val IMAGES = "images/"

        const val EXTENSION_PNG = ".png"
        const val EXTENSION_JPEG = ".jpg"

        const val MAX_QUALITY = 100
    }

    private val storage = FirebaseStorage.getInstance().reference

    fun upload(uri: Uri, contentResolver: ContentResolver, key: String, completeListener: (String) -> Unit, failureListener: (Exception) -> Unit) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        val extension = ImageHelper.getExtension(uri, contentResolver)

        val stream = ByteArrayOutputStream()

        if (extension == EXTENSION_PNG)
            bitmap.compress(Bitmap.CompressFormat.PNG, MAX_QUALITY, stream)
        else if (extension == EXTENSION_JPEG)
            bitmap.compress(Bitmap.CompressFormat.JPEG, MAX_QUALITY, stream)

        storage.child(IMAGES + key)
                .putBytes(stream.toByteArray())
                .addOnFailureListener { failureListener(it) }
                .addOnCompleteListener {
                    completeListener(it.result.downloadUrl.toString())
                }
    }
}