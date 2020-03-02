package io.bakerystud.exifdemo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single

class DataFetcher {
    private val database = FirebaseDatabase.getInstance()

    fun fetch(code: String): Single<List<PhotoRecord>> {
        return Single.create<List<PhotoRecord>> { emitter ->
            Thread.sleep(4000)
            val reference = database.getReference(code).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    //
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val photos =
                        snapshot.children.mapNotNull { it.getValue(PhotoRecord::class.java) }
                    emitter.onSuccess(photos)
                }
            })
        }
    }
}