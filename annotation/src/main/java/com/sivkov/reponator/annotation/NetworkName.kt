package com.sivkov.reponator.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class NetworkName(val get: String = "")