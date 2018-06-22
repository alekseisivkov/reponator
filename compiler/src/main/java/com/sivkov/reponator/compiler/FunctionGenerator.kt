package com.sivkov.reponator.compiler

import com.sivkov.reponator.compiler.functions.CacheFunctionProvider
import com.sivkov.reponator.compiler.functions.DbFunctionProvider
import com.sivkov.reponator.compiler.functions.NetworkFunctionProvider
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class FunctionGenerator(
        private val typeUtils: Types,
        private val elements: Elements,
        private val nameProvider: NameProvider,
        private val cacheProvider: CacheFunctionProvider,
        private val storageProvider: DbFunctionProvider,
        private val networkProvider: NetworkFunctionProvider,
        private val alterGenerator: AlterGenerator
) {

    fun generate(function: ExecutableElement) = FunSpec.overriding(function)
            .returns(function.returnType.asKotlinType())
            .addCode(buildCode(function))
            .build()

    private fun buildCode(function: ExecutableElement) = CodeBlock.builder()
            .add(buildCacheGet(function))
            .add(buildFilter(function))
            .add(buildSwitchIfEmpty(buildStorageGet(function)))
            .add(buildSwitchIfEmpty(buildNetworkGet(function)))
            .add(alterGenerator.provide(function))
            .newLine()
            .build()

    private fun buildNetworkGet(function: ExecutableElement) = CodeBlock.builder()
            .add("%L.%L(%L)", nameProvider.networkName, networkProvider.get(function), computeArguments(function))
            .add(".doOnNext { %L.%L(%L)", nameProvider.storageName, storageProvider.set(function), computeArguments(function))
            .indent()
            .indent()
            .add(".subscribe(%L) }", emptyObserver)
            .buildSaveCache(function)
            .build()

    private fun buildSwitchIfEmpty(arg: Any) = CodeBlock.builder()
            .add("%W.switchIfEmpty($arg)")
            .build()

    private fun buildStorageGet(function: ExecutableElement) = CodeBlock.builder()
            .add("%L.%L(%L)", nameProvider.storageName, storageProvider.get(function), computeArguments(function))
            .buildSaveCache(function)
            .build()

    private fun CodeBlock.Builder.buildSaveCache(function: ExecutableElement): CodeBlock.Builder {
        this.add(".doOnNext { %L.%L(%L)", nameProvider.cacheName, cacheProvider.set(function), computeArguments(function))
                .indent()
                .indent()
                .add(".subscribe(%L) }", emptyObserver)
        return this
    }

    private fun buildCacheGet(function: ExecutableElement) = CodeBlock.builder()
            .add("return %L.%L(%L)", nameProvider.cacheName, cacheProvider.get(function), computeArguments(function))
            .build()

    private fun computeArguments(function: ExecutableElement): String {
        val builder = StringBuilder()

        function.parameters.forEach {
            builder.append(it.simpleName.toString())
        }

        return builder.toString()
    }

    private val emptyObserver = "object : io.reactivex.CompletableObserver {" +
            "override fun onComplete() {}" +
            "override fun onSubscribe(d: io.reactivex.disposables.Disposable) {}" +
            "override fun onError(e: Throwable) {}}"

    private fun buildFilter(function: ExecutableElement): String {
        if (!function.returnType.isCollection()) {
            return ""
        }

        return "%W.filter { !it.isEmpty() }"
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
}