package com.sivkov.reponator.compiler

class NameProvider {

    val cacheName = cache
    val storageName = storage
    val networkName = network

    companion object {
        private const val cache = "cache"
        private const val storage = "storage"
        private const val network = "network"
    }
}