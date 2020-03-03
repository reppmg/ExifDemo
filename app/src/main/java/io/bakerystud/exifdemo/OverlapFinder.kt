package io.bakerystud.exifdemo

import android.location.Location

const val TIME_THRESHOLD = 2 * 60 * 60 * 1000L //in millis
const val DISTANCE_THRESHOLD = 50 //in meters

class OverlapFinder(
    myPhotos: List<PhotoRecord>,
    frenPhotos: List<PhotoRecord>
) {
    private val myPhotos = transformList(myPhotos)
    private val frenPhotos = transformList(frenPhotos)

    private fun transformList(target: List<PhotoRecord>): List<Photo> = target.filter {
        it.time != null && it.gps != null && it.path != null && !it.gps.nullsInside()
    }.map {
        Photo(Location("").apply {
            latitude = it.gps!!.latitude!!
            longitude = it.gps.longitude!!
        }, it.time!!, it.path!!)
    }.sortedBy { it.time }

    fun findOverlaps(): List<Overlap> {
        val allOverlaps = ArrayList<Overlap>()
        for (frenPhoto in frenPhotos) {
            val candidatesByTime = filterTime(frenPhoto)
            val overlapsForPhoto = filterPosition(frenPhoto, candidatesByTime)
            if (!overlapsForPhoto.isNullOrEmpty()) {
                val existingOverlap = allOverlaps.find {
                    it.frenPhotos.all { closeTime(frenPhoto, it) }
                            && it.frenPhotos.all { closeDistance(frenPhoto, it) }
                }
                if (existingOverlap == null) {
                    allOverlaps.add(
                        Overlap(
                            overlapsForPhoto,
                            mutableListOf(frenPhoto),
                            frenPhoto.time,
                            GpsRecord.fromLocation(frenPhoto.gps)
                        )
                    )
                } else {
                    existingOverlap.frenPhotos.add(frenPhoto)
                    //consider myPhotos already added
                    //consider date and location already set by first photo
                }
            }
        }
        return allOverlaps
    }

    private fun filterPosition(
        target: Photo,
        myPhotos: List<Photo>
    ): List<Photo> {
        return myPhotos.filter {
            closeDistance(it, target)
        }
    }

    private fun closeDistance(
        one: Photo,
        two: Photo
    ) = one.gps.distanceTo(two.gps) < DISTANCE_THRESHOLD

    private fun filterTime(target: Photo): List<Photo> {
        return myPhotos.filter {
            closeTime(target, it)
        }
    }

    private fun closeTime(
        one: Photo,
        two: Photo
    ): Boolean {
        val minTime = one.time - TIME_THRESHOLD
        val maxTime = one.time + TIME_THRESHOLD
        return two.time in minTime..maxTime
    }
}

data class Photo(
    val gps: Location,
    val time: Long,
    val path: String
)