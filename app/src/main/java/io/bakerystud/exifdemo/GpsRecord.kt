package io.bakerystud.exifdemo

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GpsRecord(
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
    }
}