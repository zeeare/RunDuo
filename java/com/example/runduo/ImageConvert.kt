package com.example.runduo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class ImageConvert {

    @TypeConverter
    fun byteArrayToBitmap(bytes: ByteArray): Bitmap
    {
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

    @TypeConverter
    fun usingBitmap(bmp: Bitmap): ByteArray
    {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }
}