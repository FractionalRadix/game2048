package com.cormontia.android.game2048

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ImageMaker {

    companion object {

        fun createImage(context: Context, gameBoardView: GameBoardView) : Uri {
            val bitmap = createImage(gameBoardView)
            return getBitmapUrl(context, bitmap)
        }

        private fun createImage(gameBoardView: GameBoardView): Bitmap {
            //TODO!~ USe the right width and height. (As derived from the actual view...)
            val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            // We may want to get the background image or color from the View.
            //  https://stackoverflow.com/a/38990869/812149
            // For now, we just set it to white.
            canvas.drawColor(Color.WHITE)
            gameBoardView.draw(canvas)
            return bitmap
        }

        private fun getBitmapUrl(context: Context, bitmap: Bitmap): Uri {
            // Saving the old way (using the deprecated "insertImage") or the new way.
            // https://stackoverflow.com/a/66817176/812149

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //TODO!~ Do something about the title.
                // Right now it sends the image as "Title.jpg", then "Title(1).jpg", etc.
                return saveImageInQ(context, bitmap, "Title")
            } else {
                //TODO!~ Do something about the title.
                // Right now it sends the image as "Title.jpg", then "Title(1).jpg", etc.
                return saveImageInLegacy(context, bitmap, "Title")
            }
        }

        private fun saveImageInLegacy(context: Context, bitmap: Bitmap, title: String): Uri {
            // Source: https://stackoverflow.com/a/38990869/812149
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val path =
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, title, null)
            return Uri.parse(path)
        }

        private fun saveImageInQ(context: Context, bitmap: Bitmap, title: String): Uri {
            var fos: OutputStream?
            var imgUri: Uri?
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, title)
                put(MediaStore.MediaColumns.MIME_TYPE, MainActivity.jpegMimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(
                    MediaStore.Images.Media.IS_PENDING,
                    1
                )  //TODO?~ Should it be Images.Media or Video.Media?
            }
            val contentResolver = context.contentResolver
            contentResolver.also { resolver ->
                imgUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imgUri?.let { resolver.openOutputStream(it) }
            }
            fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            //TODO?~ Error handling when imgUri == null. Is it possible for imgUri to be null at this point?
            contentResolver.update(imgUri!!, contentValues, null, null)
            return imgUri!!
        }
    }
}