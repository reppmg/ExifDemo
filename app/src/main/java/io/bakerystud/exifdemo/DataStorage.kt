package io.bakerystud.exifdemo

import io.reactivex.subjects.BehaviorSubject

object DataStorage {
    val subject =  BehaviorSubject.create<List<PhotoRecord>>()
}