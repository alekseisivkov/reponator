package com.sivkov.reponator.compiler

import javax.lang.model.element.ExecutableElement

class AlterNameProvider {

    private val names = mutableMapOf<ExecutableElement, String>()

    fun provide(function: ExecutableElement): String {
        return names[function] ?: generateName(function)
    }

    private fun generateName(function: ExecutableElement): String {
        var name = "${function.simpleName}Alter"
        var i = 1

        while (names.containsValue(name)) {
            name = "$name$i"
            i++
        }

        names[function] = name
        return name
    }
}