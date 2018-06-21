package com.sivkov.reponator.compiler

import com.google.auto.service.AutoService
import com.sivkov.reponator.annotation.Repository
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.inject.Inject
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
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
//                .addType(buildClass(fileName, element))
                .addType(codeGenerator.generate(fileName, element))
                .build()

        file.writeTo(filer)
    }

    private fun buildClass(fileName: String, element: TypeElement): TypeSpec = TypeSpec.classBuilder(fileName).apply {
        addSuperinterface(element.asType().asTypeName())
        appendScopes(element, this)
        appendConstructor(element, this)

        //override methods
        val methods = ElementFilter.methodsIn(element.enclosedElements)
        methods.forEach {
            addFunction(buildFun(it))
        }


    }.build()

    private fun appendScopes(element: TypeElement, builder: TypeSpec.Builder) {
        element.annotationMirrors.forEach {
            if (it.isNotInternal()) {
                builder.addAnnotation(AnnotationSpec.get(it))
            }
        }
    }

    private fun appendConstructor(element: TypeElement, builder: TypeSpec.Builder) {
        builder.primaryConstructor(FunSpec.constructorBuilder()
                .addAnnotation(AnnotationSpec.builder(Inject::class).build())
                .addParameter(ParameterSpec.builder("val $cacheName",
                        element.getAnnotation(Repository::class.java).cache(),
                        KModifier.PRIVATE)
                        .build())
                .addParameter(ParameterSpec.builder("val $storageName",
                        element.getAnnotation(Repository::class.java).db())
                        .build())
                .addParameter(ParameterSpec.builder("val $networkName",
                        element.getAnnotation(Repository::class.java).network())
                        .build())
                .build())
    }

    private fun buildFun(function: ExecutableElement) = FunSpec.overriding(function)
            .returns(function.returnType.asKotlinType())
            .addCode(buildCode(function))
            .build()


    private fun buildCode(function: ExecutableElement) = CodeBlock.builder()
            .add(buildCacheGet(function))
            .add(buildFilter(function))
            .add(buildSwitchIfEmpty(buildStorageGet(function)))
            .add(buildSwitchIfEmpty(buildNetworkGet(function)))
            .newLine()
            .build()

    private fun buildNetworkGet(function: ExecutableElement) = CodeBlock.builder()
            .add("%L.%L(%L)", networkName, computeGetFunction(function), computeArguments(function))
            .add(".doOnNext { %L.%L(%L)", storageName, computeSaveFunction(function), computeArguments(function))
            .indent()
            .indent()
            .add(".subscribe(%L) }", emptyObserver)
            .buildSaveCache(function)
            .build()

    private fun buildSwitchIfEmpty(arg: Any) = CodeBlock.builder()
            .add("%W.switchIfEmpty($arg)")
            .build()

    private fun buildStorageGet(function: ExecutableElement) = CodeBlock.builder()
            .add("%L.%L(%L)", storageName, computeGetFunction(function), computeArguments(function))
            .buildSaveCache(function)
            .build()

    private fun CodeBlock.Builder.buildSaveCache(function: ExecutableElement): CodeBlock.Builder {
        this.add(".doOnNext { %L.%L(%L)", cacheName, computeSaveFunction(function), computeArguments(function))
                .indent()
                .indent()
                .add(".subscribe(%L) }", emptyObserver)
        return this
    }


    private fun buildCacheGet(function: ExecutableElement) = CodeBlock.builder()
            .add("return %L.%L(%L)", cacheName, computeGetFunction(function), computeArguments(function))
            .build()

    private fun computeGetFunction(function: ExecutableElement): String {
        return function.simpleName.toString()
    }

    private fun computeArguments(function: ExecutableElement): String {
        val builder = StringBuilder()

        function.parameters.forEach {
            builder.append(it.simpleName.toString())
        }

        return builder.toString()
    }

    private fun computeSaveFunction(function: ExecutableElement): String {
        return "save${function.simpleName.removePrefix("get")}"
    }

    private val emptyObserver = "object : io.reactivex.CompletableObserver {\n%>%>%>" +
            "\t\t\toverride fun onComplete() {}%W" +
            "override fun onSubscribe(d: io.reactivex.disposables.Disposable) {}%W" +
            "override fun onError(e: Throwable) {}%W%<%<%< }"

    private fun buildFilter(function: ExecutableElement): String {
        if (!function.returnType.isCollection()) {
            return ""
        }

        return "%W.filter { !it.isEmpty() }"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Repository::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    private fun TypeMirror.isCollection(): Boolean {
        if (kind != TypeKind.DECLARED) {
            return false
        }

        val type = this as DeclaredType
        val list = type.typeArguments[0]
        val listElem = typeUtils.asElement(list).asType()

        val collection = typeUtils.getDeclaredType(
                elements.getTypeElement("java.util.Collection"),
                typeUtils.getWildcardType(null, null)
        )

        val map = typeUtils.getDeclaredType(
                elements.getTypeElement("java.util.Map"),
                typeUtils.getWildcardType(null, null),
                typeUtils.getWildcardType(null, null)
        )

//        messager.print("TYPE = $type list = $list listElem = $listElem collection = $collection" +
//                " isSubtype = ${typeUtils.isSubtype(listElem, collection)}" +
//                " isMapSubtype = ${typeUtils.isSubtype(listElem, map)}")

        return typeUtils.isSubtype(listElem, collection) || typeUtils.isSubtype(listElem, map)
    }

    // TODO: 18.06.2018 replace with better solution (https://github.com/square/dagger/blob/master/compiler/src/main/java/dagger/internal/codegen/Util.java)
    private fun Repository.cache(): TypeName {
        try {
            this.cache // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }

    private fun Repository.db(): TypeName {
        try {
            this.db // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }

    private fun Repository.network(): TypeName {
        try {
            this.network // this should throw
        } catch (mte: MirroredTypeException) {
            return mte.typeMirror.asTypeName()
        }

        throw IllegalStateException("Shouldn't be called")
    }

    private fun Messager.print(message: String) {
        printMessage(Diagnostic.Kind.NOTE, message)
    }

    companion object {
        const val cacheName = "cache"
        const val storageName = "storage"
        const val networkName = "network"
    }
}

