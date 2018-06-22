package com.sivkov.reponator.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class CacheName(val get: String = "", val set: String = "")