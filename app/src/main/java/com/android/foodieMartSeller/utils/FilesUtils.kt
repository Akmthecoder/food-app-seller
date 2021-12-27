package com.fitnesstraker.core.utils

import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Base64
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.android.foodieMartSeller.BuildConfig.DEBUG
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL
import kotlin.jvm.Throws


class FilesUtils {
    companion object {
        private const val TAG: String = "FilesUtils"

        const val FILE = "file"
        const val CONTENT = "content"

        val MIME_TYPE_ANSWER_ATTACHMENT = arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.oasis.opendocument.text",
            "text/rtf",
            "application/pdf",
            "text/plain",
            "odt",
            "rtf"
        )


        @Throws(FileNotFoundException::class, Exception::class)
        fun getBase64(filePath: String): String {
            val imgFile = File(filePath)
            if (imgFile.exists() && imgFile.length() > 0) {
                val bm = BitmapFactory.decodeFile(filePath)
                val bOut = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut)
                return Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
            } else {
                throw FileNotFoundException()
            }
        }

        fun download(link: String) {
            val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + ".pdf"
            URL(link).openStream().use { input ->
                FileOutputStream(File(path)).use { output ->
                    input.copyTo(output)
                }
            }
        }

        fun downloadFile(link: String, fileName: String, fileType: String, context: Context) {
            Toast.makeText(context, "Downloading start ...", Toast.LENGTH_SHORT).show()
            val request = DownloadManager.Request(Uri.parse(link))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("$fileName.$fileType")
            request.setDescription("File is downloading")
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/$fileName.$fileType")
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }


        fun getPath(context: Context, uri: Uri): String? {

            // DocumentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(
                        uri
                    )
                ) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return "${Environment.getExternalStorageDirectory()}/${split[1]}"

                    } else {
                        Toast.makeText(context, "Could not get file path. Please try again", Toast.LENGTH_SHORT).show()
                    }
                } else if (isDownloadsDocument(
                        uri
                    )
                ) {

                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            uri,
                            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
                            val fileName = cursor.getString(0)
                            val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return path
                            }
                        }
                    } finally {
                        cursor?.close()
                    }
                    //
                    val id = DocumentsContract.getDocumentId(uri)
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                        )

                        getDataColumn(
                            context,
                            contentUri,
                            null,
                            null
                        )
                    } catch (e: NumberFormatException) {
                        //In Android 8 and Android P the id is not a number
                        uri.path?.replaceFirst("^/document/raw:", "")?.replaceFirst("^raw:", "")
                    }

                } else if (isMediaDocument(
                        uri
                    )
                ) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    } else {
                        contentUri = MediaStore.Files.getContentUri("external")
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(
                        context,
                        contentUri,
                        selection,
                        selectionArgs
                    )
                }

                // DownloadsProvider
            } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                return getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                return uri.path
            }

            return null
        }

        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String>?
        ): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    if (DEBUG)
                        DatabaseUtils.dumpCursor(cursor)

                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }
            return null
        }

        fun getFile(context: Context, uri: Uri?): File? {
            if (uri != null) {
                val path =
                    getPath(
                        context,
                        uri
                    )
                if (path != null && isLocal(
                        path
                    )
                ) {
                    return File(path)
                }
            }
            return null
        }

        fun isLocal(url: String?): Boolean {
            return !(url == null || url.startsWith("http://") || url.startsWith("https://"))
        }


        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority || "com.google.android.apps.photos.contentprovider" == uri.authority
        }


        private fun isGoogleDriveUri(uri: Uri): Boolean {
            return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
        }

        private fun getDriveFilePath(uri: Uri, context: Context): String {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)
            /*
        * Get the column indexes of the data in the Cursor,
        *     * move to the first row in the Cursor, get the data,
        *     * and display it.
        * */
            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
            val file = File(context.cacheDir, name)
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable = inputStream!!.available()

                //int bufferSize = 1024;
                val bufferSize = Math.min(bytesAvailable, maxBufferSize)

                val buffers = ByteArray(bufferSize)
                read = inputStream.read(buffers)
                while (read != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            } catch (e: Exception) {

            }

            return file.path
        }
    }


}

fun File.toBase64(contentType : String): String {
    val extension = this.getExtension()
    return "data:$contentType/$extension;base64,".plus(Base64.encodeToString(this.readBytes(), Base64.NO_WRAP))
}

fun File.getMimeType(): String? {
    var mimeType: String? = ""
    val extension = this.getExtension()
    if (MimeTypeMap.getSingleton().hasExtension(extension)) {
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return mimeType
}

fun File.getExtension(): String {
    val arrayOfFilename = this.name.toCharArray()
    for (i in arrayOfFilename.size - 1 downTo 1) {
        if (arrayOfFilename[i] == '.') {
            return this.name.substring(i + 1, this.name.length)
        }
    }
    return ""
}