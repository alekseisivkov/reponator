package com.sivkov.reponator

import com.sivkov.reponator.annotation.tranformers.ObservableAlter
import io.reactivex.ObservableTransformer

class SampleAlter : ObservableAlter<String, String>() {

    override fun change(): ObservableTransformer<String, String> {
        return ObservableTransformer { it.map { it.removeRange(0..1) } }
    }

}