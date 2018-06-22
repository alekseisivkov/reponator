package com.sivkov.reponator.compiler.functions

import javax.lang.model.element.ExecutableElement

abstract class FunctionProvider<T : Annotation> {
    abstract val annotation: Class<T>

    fun get(function: ExecutableElement): String {
        val name = function.getAnnotation(annotation)
        if (name != null && validateGet(name)) {
            return parseGet(name)
        }
        return function.simpleName.toString()
    }

    protected abstract fun validateGet(annotation: T): Boolean

    protected abstract fun parseGet(annotation: T): String

    fun set(function: ExecutableElement): String {
        val name = function.getAnnotation(annotation)
        if (name != null && validateSet(name)) {
            return parseSet(name)
        }
        return "save${function.simpleName.removePrefix("get")}"
    }

    protected abstract fun validateSet(annotation: T): Boolean

    protected abstract fun parseSet(annotation: T): String

}