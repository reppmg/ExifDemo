package io.bakerystud.exifdemo

import io.reactivex.subjects.BehaviorSubject

object DataStorage {
    val frenPhotos =  BehaviorSubject.create<List<PhotoRecord>>()
    val myPhotos =  BehaviorSubject.create<List<PhotoRecord>>()
}