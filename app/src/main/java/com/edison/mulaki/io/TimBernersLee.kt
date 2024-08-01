package com.edison.mulaki.io

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

class TimBernersLee {

    private val lineEnd = "\r\n"
    private val twoHyphens = "--"
    private val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"

    val ioException = "Network Error"
    val connectTimeout = 5000

    fun httpGet(url:String):String{
        try {
            var uri = URL(url)
            var urlConnection = uri.openConnection() as HttpURLConnection

            urlConnection.connectTimeout = connectTimeout

            val responseCode = urlConnection.responseCode

            if(responseCode == HttpURLConnection.HTTP_OK){
                val inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuffer()

                var inputLine = bufferedReader.readLine()

                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = bufferedReader.readLine()
                }
                bufferedReader.close()
                return response.toString()
            } else {
                val errorStream = urlConnection.errorStream
                if (errorStream != null) {
                    val errorResponse = BufferedReader(InputStreamReader(errorStream)).use {
                        it.readText()
                    }
                    throw Exception(errorResponse)
                }
                throw Exception("Some Error")
            }
        }catch (e:IOException){
            println(e)
            throw IOException(ioException)
        }
    }

    fun httpPost(url:String, jsonObject: JSONObject):String{
        try {
            var uri = URL(url)
            var urlConnection = uri.openConnection() as HttpURLConnection

            urlConnection.connectTimeout = connectTimeout
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            urlConnection.doOutput = true

            val data = jsonObject

            val outputStream = urlConnection.outputStream
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
            writer.write(data.toString())
            writer.flush()
            writer.close()
            outputStream.close()

            val responseCode = urlConnection.responseCode

            if(responseCode == HttpURLConnection.HTTP_OK){
                val inputStream = urlConnection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuffer()

                var inputLine = bufferedReader.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = bufferedReader.readLine()
                }
                bufferedReader.close()
                return response.toString()
            }else{
                val errorStream = urlConnection.errorStream
                if (errorStream != null) {
                    val errorResponse = BufferedReader(InputStreamReader(errorStream)).use {
                        it.readText()
                    }
                    throw Exception(errorResponse)
                }
                throw Exception("Some Error")
            }
        }catch (e:IOException){
            println(e)
            throw IOException(ioException)
        }
    }


    fun uploadFiles(apiUrl: String, _uploadProgress: MutableLiveData<String>,  files: MutableList<File>, jsonObject:JSONObject): String {
        try {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection

            val bufferSize = 1024 * 10

            connection.connectTimeout = connectTimeout
            connection.doInput = true
            connection.doOutput = true
            connection.useCaches = false
            connection.setChunkedStreamingMode(bufferSize)

            connection.requestMethod = "POST"
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

            val outputStream = DataOutputStream(connection.outputStream)

            jsonFormData(outputStream, jsonObject)

            val buffer = ByteArray(bufferSize)
            var bytesRead: Int

            var uploadedFileSize: Long = 0

            files.forEachIndexed {index, file ->
                fileFormData(outputStream, if (index == 0) "file" else "file$index", file.name)

                val inputStream = FileInputStream(file)
                val fileSize = file.length()

                while (inputStream.read(buffer, 0, bufferSize).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)

                    uploadedFileSize += bytesRead.toLong()
                    val progress = ((uploadedFileSize.toFloat() / fileSize) * 100).toInt()
                    _uploadProgress.postValue(progress.toString())
                }

                inputStream.close()

                outputStream.writeBytes(lineEnd)
            }

            outputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

            outputStream.flush()
            outputStream.close()

            if(connection.responseCode == HttpURLConnection.HTTP_OK){
                return "File upload successful"
            } else {
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    val errorResponse = BufferedReader(InputStreamReader(errorStream)).use {
                        it.readText()
                    }
                    throw Exception(errorResponse)
                }
                throw Exception("Some Error")
            }
        } catch (e:IOException){
            println(e)
            throw IOException(ioException)
        }
    }


    fun downloadFileAndSave(_downloadProgress: MutableLiveData<String>, context: Context, fileUrl:String, fileName: String):String{
        try {
            val url = URL(fileUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = connectTimeout
            connection.connect()
            if(connection.responseCode == HttpURLConnection.HTTP_OK){
                val input: InputStream = connection.inputStream
                val extName = AndreyBreslav().extname(fileName)
                return if(extName == ".png" || extName == ".jpg"){
                    AndyRubin().saveToGallery(context, input, fileName, extName)
                }else{
                    AndyRubin().saveToDownloads(_downloadProgress, input, fileName, connection.contentLength.toLong())
                }
            } else {
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    val errorResponse = BufferedReader(InputStreamReader(errorStream)).use {
                        it.readText()
                    }
                    throw Exception(errorResponse)
                }
                throw Exception("Some Error")
            }
        }catch (e:IOException){
            println(e)
            throw IOException(ioException)
        }
    }

    fun checkSocketConnection(host: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            val socketAddress = InetSocketAddress(host, port)
            socket.connect(socketAddress, connectTimeout)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
    private fun jsonFormData(outputStream: DataOutputStream, value:Any){
        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"body\"$lineEnd")
        outputStream.writeBytes(lineEnd)
        outputStream.write(value.toString().toByteArray())
        outputStream.writeBytes(lineEnd)
    }

    private fun fileFormData(outputStream: DataOutputStream, field:String, fileName:String){
        outputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        outputStream.writeBytes("Content-Disposition: form-data; name=\"$field\"; filename=\"${fileName}\"$lineEnd")
        outputStream.writeBytes("Content-Type: application/octet-stream$lineEnd")
        outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd)
        outputStream.writeBytes(lineEnd)

    }

}