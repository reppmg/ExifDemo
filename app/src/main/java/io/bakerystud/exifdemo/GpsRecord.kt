package io.bakerystud.exifdemo

import android.location.Location
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GpsRecord(
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    companion object {
        fun fromLocation(location: Location): GpsRecord {
            return GpsRecord(location.latitude, location.longitude)
        }
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
    }

    fun nullsInside(): Boolean = latitude == null || longitude == null
}

