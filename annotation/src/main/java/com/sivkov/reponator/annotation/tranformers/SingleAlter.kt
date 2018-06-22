package com.sivkov.reponator.annotation.tranformers

import io.reactivex.SingleTransformer

abstract class SingleAlter<Upstream : Any, Downstream : Any> : Alter {
    abstract fun change(): SingleTransformer<Upstream, Downstream>
}