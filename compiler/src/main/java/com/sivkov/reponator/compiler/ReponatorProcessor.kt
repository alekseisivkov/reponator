package com.sivkov.reponator.compiler

import com.google.auto.service.AutoService
import com.sivkov.reponator.annotation.Repository
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


@AutoService(Processor::class)
class ReponatorProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private lateinit var filer: Filer
    private lateinit var typeUtils: Types
    private lateinit var codeGenerator: CodeGenerator

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        codeGenerator = CodeGenerator(processingEnv)
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        filer = processingEnv.filer
        typeUtils = processingEnv.typeUtils
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        messager.printMessage(Diagnostic.Kind.NOTE, "Start processing")

        roundEnv.getElementsAnnotatedWith(Repository::class.java).forEach {
            if (it.kind != ElementKind.INTERFACE) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation @Repository not on the interface. ${it.kind}")
                return true
            }

            processElement(it as TypeElement)
        }

        return true
    }

    private fun processElement(element: TypeElement) {
        val pkg = elements.getPackageOf(element).toString()
        val fileName = element.simpleName.toString().plus("Impl")

        val file = FileSpec.builder(pkg, fileName)
                .addType(codeGenerator.generate(fileName, element))
                .build()

        file.writeTo(filer)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Repository::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}

