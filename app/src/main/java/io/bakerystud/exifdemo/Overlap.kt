package io.bakerystud.exifdemo

data class Overlap(
    val myPhotos: List<Photo>,
    val frenPhotos: MutableList<Photo>,
    val date: Long,
    val gpsRecord: GpsRecord
)