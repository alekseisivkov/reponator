package com.sivkov.reponator.annotation

import com.sivkov.reponator.annotation.tranformers.Alter
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Transform(val alter: KClass<out Alter>)