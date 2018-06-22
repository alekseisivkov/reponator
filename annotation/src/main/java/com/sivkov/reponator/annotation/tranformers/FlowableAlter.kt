package com.sivkov.reponator.annotation.tranformers

import io.reactivex.FlowableTransformer

abstract class FlowableAlter<Upstream : Any, Downstream : Any> : Alter {
    abstract fun change(): FlowableTransformer<Upstream, Downstream>
}