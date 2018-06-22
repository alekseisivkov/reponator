package com.sivkov.reponator.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.inject.Inject
import javax.lang.model.element.TypeElement

class ConstructorGenerator(private val args: ArgProvider, private val names: NameProvider) {

    fun generate(element: TypeElement, builder: TypeSpec.Builder) {
        builder.primaryConstructor(FunSpec.constructorBuilder()
                .addAnnotation(AnnotationSpec.builder(Inject::class).build())
                .addParameter(ParameterSpec.builder("val ${names.cacheName}",
                        args.get(Args.Cache(), element),
                        KModifier.PRIVATE)
                        .build())
                .addParameter(ParameterSpec.builder("val ${names.storageName}",
                        args.get(Args.Storage(), element),
                        KModifier.PRIVATE)
                        .build())
                .addParameter(ParameterSpec.builder("val ${names.networkName}",
                        args.get(Args.Network(), element),
                        KModifier.PRIVATE)
                        .build())
                .build())
    }
}