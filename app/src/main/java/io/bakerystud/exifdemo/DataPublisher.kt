package io.bakerystud.exifdemo

import com.google.firebase.database.FirebaseDatabase

class DataPublisher {
    private val database = FirebaseDatabase.getInstance().reference

    fun publish() {
        if (alreadyPublished()) return

        val testPhoto = PhotoRecord(GpsRecord(12.0, 11.0), System.currentTimeMillis())
        database.child("test").setValue(testPhoto)
    }

    private fun alreadyPublished(): Boolean {
        return false //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}