package com.sivkov.reponator.compiler.functions

import com.sivkov.reponator.annotation.NetworkName

class NetworkFunctionProvider: FunctionProvider<NetworkName>() {

    override val annotation: Class<NetworkName> = NetworkName::class.java

    override fun validateGet(annotation: NetworkName) = annotation.get.isNotBlank()

    override fun parseGet(annotation: NetworkName) = annotation.get

    override fun validateSet(annotation: NetworkName): Boolean {
        throw UnsupportedOperationException("No set function for network requests")
    }

    override fun parseSet(annotation: NetworkName): String {
        throw UnsupportedOperationException("No set function for network requests")
    }
}