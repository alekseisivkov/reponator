package com.sivkov.reponator.annotation.tranformers

import io.reactivex.CompletableTransformer

abstract class CompletableAlter : Alter {
    abstract fun change(): CompletableTransformer
}