package com.sivkov.reponator.annotation.tranformers

import io.reactivex.ObservableTransformer

abstract class ObservableAlter<Upstream : Any, Downstream : Any> : Alter {
    abstract fun change(): ObservableTransformer<Upstream, Downstream>
}