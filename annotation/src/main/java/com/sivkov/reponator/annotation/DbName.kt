package com.sivkov.reponator.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class DbName(val get: String = "", val set: String = "")