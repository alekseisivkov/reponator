package com.sivkov.reponator.annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class CustomNames(val cacheGet: String = "",
                             val cacheSet: String = "")