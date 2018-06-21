package com.sivkov.reponator.compiler

import com.sivkov.reponator.annotation.Repository
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException

class ArgProvider {

    fun get(arg: Args, element: TypeElement) = when (arg) {
        is Args.Cache -> getCache(element)
        is Args.Storage -> getStorage(element)
        is Args.Network -> getNetwork(element)
    }

    private fun getCache(element: TypeElement) = element.getAnnotation(Repository::class.java).cache()
    private fun getStorage(element: TypeElement) = element.getAnnotation(Repository::class.java).db()
    private fun getNetwork(element: TypeElement) = element.getAnnotation(Repository::class.java).network()

    // TODO: 18.06.2018 replace with better solution (https://github.com/square/dagger/blob/master/compiler/src/main/java/dagger/internal/codegen/Util.java)
    private fun Repository.cache(): TypeName {
        try {
            this.cache // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }

    private fun Repository.db(): TypeName {
        try {
            this.db // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }

    private fun Repository.network(): TypeName {
        try {
            this.network // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }
}

sealed class Args {
    class Cache : Args()
    class Storage : Args()
    class Network : Args()
}