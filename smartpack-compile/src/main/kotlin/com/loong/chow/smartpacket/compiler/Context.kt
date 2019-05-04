package com.loong.chow.smartpacket.compiler

import javax.annotation.processing.ProcessingEnvironment

class Context {

    companion object {
        lateinit var processingEnvironment: ProcessingEnvironment
        val elementUtils by lazy {
            processingEnvironment.elementUtils
        }
        val messenger by lazy {
            processingEnvironment.messager;
        }
        val typeUtils by lazy {
            processingEnvironment.typeUtils;
        }
    }
}