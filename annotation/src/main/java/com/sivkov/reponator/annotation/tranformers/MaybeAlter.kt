package com.sivkov.reponator.annotation.tranformers

import io.reactivex.MaybeTransformer

abstract class MaybeAlter<Upstream : Any, Downstream : Any> : Alter {
    abstract fun change(): MaybeTransformer<Upstream, Downstream>
}