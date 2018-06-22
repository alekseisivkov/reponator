package com.sivkov.reponator.compiler.functions

import com.sivkov.reponator.annotation.CacheName

class CacheFunctionProvider : FunctionProvider<CacheName>() {
    override val annotation = CacheName::class.java

    override fun validateGet(annotation: CacheName) = annotation.get.isNotBlank()

    override fun parseGet(annotation: CacheName) = annotation.get

    override fun validateSet(annotation: CacheName) = annotation.set.isNotBlank()

    override fun parseSet(annotation: CacheName) = annotation.set

}