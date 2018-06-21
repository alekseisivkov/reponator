package com.sivkov.reponator.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.StandardLocation
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.platform.JavaToKotlinClassMap


@Throws(IOException::class)
fun FileSpec.writeTo(filer: Filer) {
    val originatingElements = mutableListOf<TypeElement>()
    val filerSourceFile = filer.createResource(StandardLocation.SOURCE_OUTPUT,
            packageName, "$name.kt", *originatingElements.toTypedArray())
    try {
        filerSourceFile.openWriter().use { writer -> writeTo(writer) }
    } catch (e: Exception) {
        try {
            filerSourceFile.delete()
        } catch (ignored: Exception) {
        }
        throw e
    }
}


fun AnnotationMirror.isNotInternal() = annotationType.asElement().asType().toString() != "kotlin.Metadata" &&
        annotationType.asElement().asType().toString() != "com.sivkov.reponator.annotation.Repository"

fun CodeBlock.Builder.newLine(): CodeBlock.Builder {
    this.add("\n")
    return this
}

fun TypeMirror.asKotlinType(): TypeName =
        asTypeName().javaToKotlinType()

private fun TypeName.javaToKotlinType(): TypeName {
    return if (this is ParameterizedTypeName) {
        ParameterizedTypeName.get(
                rawType.javaToKotlinType() as ClassName,
                *typeArguments.map { it.javaToKotlinType() }.toTypedArray()
        )
    } else {
        val className =
                JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))
                        ?.asSingleFqName()?.asString()

        return if (className == null) {
            this
        } else {
            ClassName.bestGuess(className)
        }
    }
}