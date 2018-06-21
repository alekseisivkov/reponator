package com.sivkov.reponator.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Repository(val cache: KClass<out Any> = Repository::class,
                            val db: KClass<out Any> = Repository::class,
                            val network: KClass<out Any> = Repository::class)
