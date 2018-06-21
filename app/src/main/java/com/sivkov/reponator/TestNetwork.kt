package com.sivkov.reponator

import io.reactivex.Flowable
import io.reactivex.Observable

class TestNetwork {

    fun getName(): Observable<String> {
        return Observable.empty()
    }

    fun getNames(): Flowable<Map<String, String>> {
        return Flowable.fromCallable { mapOf("Aleksei" to "one", "Igor" to "two", "Wadges" to "three") }
    }

    fun getName(index: Int) : Observable<String> {
        return Observable.fromCallable { "Name $index" }
    }
}