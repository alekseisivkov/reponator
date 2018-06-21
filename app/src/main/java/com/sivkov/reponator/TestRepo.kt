package com.sivkov.reponator

import com.sivkov.reponator.annotation.CustomNames
import com.sivkov.reponator.annotation.Repository
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Singleton

@Repository(cache = TestCache::class, db = TestDb::class, network = TestNetwork::class)
@Singleton
interface TestRepo {

    @CustomNames("get")
    fun getName(): Observable<String>

    fun getNames(): Flowable<Map<String, String>>

    fun getName(index: Int) : Observable<String>
}
