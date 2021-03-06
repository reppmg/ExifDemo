package io.bakerystud.exifdemo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DataPublisher(
    private val context: Context,
    private val deviceId: String
) {
    private val database = FirebaseDatabase.getInstance().reference

    fun publish() {
        val records: List<Pair<PhotoRecord, String>> =
            getAllPhotosList().mapNotNull {
                pathToRecord(it)
            }.map {
                it to it.path!!.split("/").last().split(".").first()
            }
        records.forEach {
            val (record, name) = it
            database.child(deviceId).child(name).setValue(record)
        }
        DataStorage.myPhotos.onNext(records.map { it.first })
        Timber.d("publish finished")
    }

    private fun uploadLastFile(records: List<Pair<PhotoRecord, String>>) {
        val record = records.last().first
        val path = record.path!!
        val file = File(path)
        val bytes = file.readBytes()
        val base64string = Base64.encodeToString(bytes, Base64.DEFAULT)
        database.child(deviceId).child(path.split("/").last().split(".").first())
            .setValue(base64string)
    }

    private fun pathToRecord(path: String): PhotoRecord? {
        try {
            val exif = ExifInterface(path)
            val timeString =
                exif.getAttribute(android.media.ExifInterface.TAG_DATETIME)
            val latLng = exif.latLong ?: doubleArrayOf(0.0, 0.0)

            val date =
                if (!timeString.isNullOrEmpty()) SimpleDateFormat(
                    "yyyy:MM:dd HH:mm:ss",
                    Locale.getDefault()
                ).parse(timeString) else null

            return PhotoRecord(
                GpsRecord(
                    latLng[0],
                    latLng[1]
                ),
                date?.time,
                path
            )
        } catch (e: Exception) {
            return null
        }
    }

    @SuppressLint("InlinedApi", "Recycle")
    fun getAllPhotosList(): List<String> {
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages =
            ArrayList<String?>()
        var absolutePathOfImage: String? = null
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val cursor = context.getContentResolver().query(
            uri, projection, null,
            null, null
        )!!

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            if (absolutePathOfImage.contains("DCIM")) listOfAllImages.add(absolutePathOfImage)
        }
        cursor.close()
        return listOfAllImages.filterNotNull()
    }
}