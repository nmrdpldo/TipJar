package com.example.tip_jar.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

class Utils {
    companion object{

        fun createImageFile(context: Context): File {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", //prefix
                ".jpg", //suffix
                storageDir //directory
            )
        }

        fun checkCameraPermission(context : Context) : Boolean {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

            return permissionCheckResult == PackageManager.PERMISSION_GRANTED
        }

        fun converterBitmapToString(bitmap: Bitmap?): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun converterStringToBitmap(encodedString: String): Bitmap? {
            return try {
                val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e:Exception) {
                e.printStackTrace()
                null
            }
        }

        fun converterURItoBitmap(context : Context, uri : Uri) : Bitmap? {
            val contentResolver: ContentResolver = context.contentResolver
            var inputStream: InputStream? = null
            try {
                inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    return BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
            }
            return null
        }

        fun checkImageOrientation(bmInput: Bitmap?, filePath: String) : Bitmap? {
            try {
                val exif = ExifInterface(filePath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val matrix = Matrix()
                when (orientation) {
                    6 -> {
                        matrix.postRotate(90f);
                    }
                    3 -> {
                        matrix.postRotate(180f);
                    }
                    8 -> {
                        matrix.postRotate(270f);
                    }
                }
                return Bitmap.createBitmap(bmInput!!, 0, 0, bmInput.width, bmInput.height, matrix, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        fun roundOffTwoDecimals(input : Double) : Double{
            return try{
                round(input * 100 ) / 100
            }catch (e : Exception){
                input
            }
        }
    }
}