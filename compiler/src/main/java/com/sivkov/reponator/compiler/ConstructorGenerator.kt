package com.sivkov.reponator.compiler

import com.sivkov.reponator.annotation.Transform
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.inject.Inject
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException

class ConstructorGenerator(
        private val args: ArgProvider,
        private val names: NameProvider,
        private val nameProvider: AlterNameProvider
) {

    fun generate(element: TypeElement, builder: TypeSpec.Builder) {
        val constructor = FunSpec.constructorBuilder()
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
                        KModifier.PRIVATE).build())
                .addAlterParameters(element)

        builder.primaryConstructor(constructor.build())
    }

    private fun FunSpec.Builder.addAlterParameters(element: TypeElement): FunSpec.Builder {
        element.enclosedElements.forEach {
            if (it.kind == ElementKind.METHOD && it.getAnnotation(Transform::class.java) != null) {
                this.addAlter(it as ExecutableElement)
            }
        }
        return this
    }

    private fun FunSpec.Builder.addAlter(element: ExecutableElement) {
        val alter = element.getAnnotation(Transform::class.java).alter()

        addParameter(ParameterSpec.builder("val ${nameProvider.provide(element)}", alter, KModifier.PRIVATE).build())
    }

    // TODO: 18.06.2018 replace with better solution (https://github.com/square/dagger/blob/master/compiler/src/main/java/dagger/internal/codegen/Util.java)
    private fun Transform.alter(): TypeName {
        try {
            this.alter // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }
}