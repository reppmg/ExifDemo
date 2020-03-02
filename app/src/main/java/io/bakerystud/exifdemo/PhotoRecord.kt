package io.bakerystud.exifdemo

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PhotoRecord(
    val gps: GpsRecord? = null,
    val time: Long? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "gps" to gps,
            "time" to time
        )
    }
}