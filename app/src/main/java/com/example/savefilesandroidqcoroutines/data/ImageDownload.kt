package com.example.savefilesandroidqcoroutines.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.multidex.BuildConfig
import com.example.savefilesandroidqcoroutines.utils.extensions.getImageUrlExtension
import com.example.savefilesandroidqcoroutines.utils.extensions.getMimeType
import com.example.savefilesandroidqcoroutines.utils.extensions.getRandomString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import java.io.File

private val PROJECTION = arrayOf(MediaStore.Video.Media._ID)
private const val QUERY = MediaStore.Video.Media.DISPLAY_NAME + " = ?"
private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

class ImageDownload(private val context: Context) {
    private val ok = OkHttpClient()
    private val collection =
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    suspend fun getLocalUri(filename: String): Uri? =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            resolver.query(collection, PROJECTION, QUERY, arrayOf(filename), null)
                ?.use { cursor ->
                    if (cursor.count > 0) {
                        cursor.moveToFirst()
                        return@withContext ContentUris.withAppendedId(
                            collection,
                            cursor.getLong(0)
                        )
                    }
                }

            null
        }

    suspend fun download(filename: String): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) downloadQ(filename)
        else downloadLegacy(filename)

    private suspend fun downloadQ(url: String): String =
        withContext(Dispatchers.IO) {
            val response = ok.newCall(Request.Builder().url(url).build()).execute()

            if (response.isSuccessful) {
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, "${getRandomString()}${url.getImageUrlExtension()}")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    put(MediaStore.Video.Media.MIME_TYPE, url.getMimeType()) //put(MediaStore.Video.Media.MIME_TYPE, "image/gif")
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(collection, values)

                uri?.let {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        val sink = Okio.buffer(Okio.sink(outputStream))

                        response.body()?.source()?.let { sink.writeAll(it) }
                        sink.close()
                    }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                } ?: throw RuntimeException("MediaStore failed for some reason")

                uri.encodedPath ?: throw RuntimeException("Can't get image path for some reason")
            } else {
                throw RuntimeException("OkHttp failed for some reason")
            }
        }

    private suspend fun downloadLegacy(url: String): String =
        withContext(Dispatchers.IO) {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "${getRandomString()}${url.getImageUrlExtension()}"
            )
            val response = ok.newCall(Request.Builder().url(url).build()).execute()

            if (response.isSuccessful) {
                val sink = Okio.buffer(Okio.sink(file))

                response.body()?.source()?.let { sink.writeAll(it) }
                sink.close()

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf(url.getMimeType()),
                    null
                )

                FileProvider.getUriForFile(context, AUTHORITY, file).encodedPath ?: throw RuntimeException("Can't get image path for some reason")
            } else {
                throw RuntimeException("OkHttp failed for some reason")
            }
        }
}