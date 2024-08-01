package com.edison.mulaki.io

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class AndyRubin {

    fun readContactsPermission(context: Context):Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun writeExternalStoragePermission(context: Context):Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun readExternalStoragePermission(context: Context):Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun saveToGallery(context: Context, inputStream: InputStream, displayName: String, extName:String):String {

        val mimetype:String = if (extName ==".png") { "image/png" } else { "image/jpeg" }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, displayName);
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.DESCRIPTION, displayName);
            put(MediaStore.Images.Media.MIME_TYPE, mimetype);
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }

        val resolver = context.contentResolver
        var outputStream: OutputStream? = null

        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val uri = resolver.insert(contentUri, contentValues)
            if (uri != null) {
                outputStream = resolver.openOutputStream(uri)

                return if (outputStream != null) {
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }

                    "Image successfully saved to gallery"
                } else {
                    "OutputStream is null"
                }
            } else {
                return "Uri is null"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return e.toString()
        } finally {
            outputStream?.close()
        }
    }

    fun saveToDownloads(_downloadProgress: MutableLiveData<String>, inputStream: InputStream, fileName: String, fileSize:Long?):String {
        try {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName)

            val outputStream = FileOutputStream(file)

            val bufferSize = 1024 * 10
            val buffer = ByteArray(bufferSize)
            var bytesRead: Int
            var downloadedFileSize: Long = 0

            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                outputStream.write(buffer, 0, bytesRead)

                downloadedFileSize += bytesRead.toLong()
                if (fileSize!=null){
                    val progress = (downloadedFileSize * 100 / fileSize).toInt()
                    _downloadProgress.postValue(progress.toString())
                }

            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            return "The file was successfully saved to the gallery"
        } catch (e: IOException) {
            e.printStackTrace()
            return e.toString()
        }
    }


    fun saveFileByDirectory(context: Context, inputStream: InputStream, url:String, fileName:String):String{
        try {
            val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), url)
            directory.mkdirs()

            val file = File(directory, fileName)
            val output: OutputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            output.close()

            return "Dosya başarıyla galeriye kaydedildi"
        }catch (e:Exception){
            return e.toString()

        }

    }

    val supportedMimeTypes = listOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp",

        "video/mp4",
        "video/3gpp",
        "video/x-msvideo",
        "video/x-flv",
        "video/x-matroska",
        "video/webm",

        "audio/mpeg",
        "audio/mp4",
        "audio/amr",
        "audio/x-wav",
        "audio/ogg",
        "audio/aac"
    )

    fun mimetype(mimeType: String): String {
        return when {
            supportedMimeTypes.contains(mimeType) -> {
                when {
                    mimeType.startsWith("image/") -> "image"
                    mimeType.startsWith("video/") -> "video"
                    mimeType.startsWith("audio/") -> "audio"
                    else -> "unknown"
                }
            }
            else -> "file"
        }
    }

    fun mimetypeByExtension(extension: String?):String{
        var mimetype:String? = null
        if (extension != null) {
           mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimetype!!;
    }
    fun imageDimensions(context: Context?, imageUri: Uri): Size? {
        var width: Int = 0
        var height: Int = 0
        try {
            val inputStream: InputStream? = context!!.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream!!.close()
            width = bitmap.width
            height = bitmap.height
        } catch (e: Exception) {
            Toast.makeText(context, "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
        if (width > 0 && height > 0) {
            return Size(width, height)
        }
        return null
    }

    fun fileFromContentUri(context: Context?, contentUri: Uri,): File {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context?.contentResolver?.query(contentUri, filePathColumn, null, null, null)

        cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(filePathColumn[0])
            val filePath = it.getString(columnIndex)
            return File(filePath)
        }

        throw RuntimeException("File path could not be retrieved from Content URI.")
    }
}