package com.sivkov.reponator.compiler

import com.sivkov.reponator.annotation.Transform
import com.squareup.kotlinpoet.CodeBlock
import javax.lang.model.element.ExecutableElement

class AlterGenerator(private val nameProvider: AlterNameProvider) {

    fun provide(function: ExecutableElement): CodeBlock {
        if (function.getAnnotation(Transform::class.java) == null) {
            return CodeBlock.builder().build()
        }

        return CodeBlock.builder()
                .add("%W.compose(%L.change())", nameProvider.provide(function))
                .build()
    }
}