package com.sivkov.reponator.compiler

import com.sivkov.reponator.compiler.functions.CacheFunctionProvider
import com.sivkov.reponator.compiler.functions.DbFunctionProvider
import com.sivkov.reponator.compiler.functions.NetworkFunctionProvider
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

class CodeGenerator(processingEnv: ProcessingEnvironment) {
    private val messager = processingEnv.messager
    private val elements = processingEnv.elementUtils
    private val filer = processingEnv.filer
    private val typeUtils = processingEnv.typeUtils

    private val argProvider = ArgProvider()
    private val cacheFunctionProvider = CacheFunctionProvider()
    private val storageFunctionProvider = DbFunctionProvider()
    private val networkFunctionProvider = NetworkFunctionProvider()
    private val nameProvider = NameProvider()
    private val alterNameProvider = AlterNameProvider()
    private val annotationGenerator = AnnotationGenerator()
    private val constructorGenerator = ConstructorGenerator(argProvider, nameProvider, alterNameProvider)
    private val alterGenerator = AlterGenerator(alterNameProvider)
    private val functionGenerator = FunctionGenerator(typeUtils, elements, nameProvider, cacheFunctionProvider, storageFunctionProvider, networkFunctionProvider, alterGenerator)


    fun generate(fileName: String, element: TypeElement) = TypeSpec.classBuilder(fileName).apply {
        addSuperinterface(element.asType().asTypeName())
        annotationGenerator.generate(element, this)
        constructorGenerator.generate(element, this)

        //override methods
        val methods = ElementFilter.methodsIn(element.enclosedElements)
        methods.forEach {
            addFunction(functionGenerator.generate(it))
        }


    }.build()

}