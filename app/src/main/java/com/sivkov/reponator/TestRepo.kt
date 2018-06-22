package com.sivkov.reponator

import com.sivkov.reponator.annotation.CacheName
import com.sivkov.reponator.annotation.DbName
import com.sivkov.reponator.annotation.NetworkName
import com.sivkov.reponator.annotation.Repository
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Singleton

@Repository(cache = TestCache::class, db = TestDb::class, network = TestNetwork::class)
@Singleton
interface TestRepo {

    @CacheName("get")
    @DbName(set = "setSingleName")
    @NetworkName("downloadName")
    fun getName(): Observable<String>

    fun getNames(): Flowable<Map<String, String>>

    fun getName(index: Int): Observable<String>
}
