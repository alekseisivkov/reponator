package com.sivkov.reponator.compiler

import com.sivkov.reponator.annotation.CustomNames
import javax.lang.model.element.ExecutableElement

class CacheFunctionProvider {

    fun provideGet(function: ExecutableElement): String {
        val customNames = function.getAnnotation(CustomNames::class.java)
        if (customNames != null && customNames.cacheGet.isNotBlank()) {
            return customNames.cacheGet
        }
        return function.simpleName.toString()
    }

    // TODO: 19.06.2018 это сделано, надо сделать для остальных типов база + сеть
    fun provideSet(function: ExecutableElement): String {
        val customNames = function.getAnnotation(CustomNames::class.java)
        if (customNames != null && customNames.cacheSet.isNotBlank()) {
            return customNames.cacheSet
        }
        return "save${function.simpleName.removePrefix("get")}"
    }
}