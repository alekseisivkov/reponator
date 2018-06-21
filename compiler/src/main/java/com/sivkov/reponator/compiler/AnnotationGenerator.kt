package com.sivkov.reponator.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.TypeElement

class AnnotationGenerator {

    fun generate(element: TypeElement, builder: TypeSpec.Builder) {
        element.annotationMirrors.forEach {
            if (it.isNotInternal()) {
                builder.addAnnotation(AnnotationSpec.get(it))
            }
        }
    }
}