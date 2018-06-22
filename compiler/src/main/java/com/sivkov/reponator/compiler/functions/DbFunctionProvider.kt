package com.sivkov.reponator.compiler.functions

import com.sivkov.reponator.annotation.DbName

class DbFunctionProvider : FunctionProvider<DbName>() {

    override val annotation: Class<DbName> = DbName::class.java

    override fun validateGet(annotation: DbName) = annotation.get.isNotBlank()

    override fun parseGet(annotation: DbName) = annotation.get

    override fun validateSet(annotation: DbName) = annotation.set.isNotBlank()

    override fun parseSet(annotation: DbName) = annotation.set

}