package com.sivkov.reponator

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestCache @Inject constructor() {

    fun get(): Observable<String> {
//        return Observable.fromCallable { "Single Name" }
        return Observable.empty()
    }

    fun saveName(): Completable {
        return Completable.complete()
    }

    fun saveName(index: Int): Completable {
        return Completable.complete()
    }

    fun saveNames(): Completable {
        return Completable.complete()
    }

    fun getNames(): Flowable<Map<String, String>> {
        return Flowable.fromCallable { mapOf("Aleksei" to "one", "Igor" to "two", "Wadges" to "three") }
    }

    fun getName(index: Int) : Observable<String> {
        return Observable.fromCallable { "Name $index" }
    }
}
