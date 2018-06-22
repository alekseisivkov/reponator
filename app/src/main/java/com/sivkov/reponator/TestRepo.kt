package com.sivkov.reponator

import com.sivkov.reponator.annotation.CacheName
import com.sivkov.reponator.annotation.DbName
import com.sivkov.reponator.annotation.NetworkName
import com.sivkov.reponator.annotation.Repository
import com.sivkov.reponator.annotation.Transform
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Singleton

@Repository(cache = TestCache::class, db = TestDb::class, network = TestNetwork::class)
@Singleton
interface TestRepo {

    @CacheName("get")
    @DbName(set = "setSingleName")
    @NetworkName("downloadName")
    @Transform(SampleAlter::class)
    fun getName(): Observable<String>

    fun getNames(): Flowable<Map<String, String>>

    @Transform(SampleAlter::class)
    fun getName(index: Int): Observable<String>

    @Transform(AnotherAlter::class)
    fun getName(key: Double): Observable<String>
}
