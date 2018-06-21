package com.sivkov.reponator.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.inject.Inject
import javax.lang.model.element.TypeElement

class ConstructorGenerator(private val provider: ArgProvider) {

    fun generate(element: TypeElement, builder: TypeSpec.Builder) {
        builder.primaryConstructor(FunSpec.constructorBuilder()
                .addAnnotation(AnnotationSpec.builder(Inject::class).build())
                .addParameter(ParameterSpec.builder("val ${ReponatorProcessor.cacheName}",
                        provider.get(Args.Cache(), element),
                        KModifier.PRIVATE)
                        .build())
                .addParameter(ParameterSpec.builder("val ${ReponatorProcessor.storageName}",
                        provider.get(Args.Storage(), element),
                        KModifier.PRIVATE)
                        .build())
                .addParameter(ParameterSpec.builder("val ${ReponatorProcessor.networkName}",
                        provider.get(Args.Network(), element),
                        KModifier.PRIVATE)
                        .build())
                .build())
    }
}