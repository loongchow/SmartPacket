package com.loong.chow.smartpacket.compiler.converter

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName

interface TypeConvertInterface {
    fun convert(builder: MethodSpec.Builder, value: String, result: String)
    fun revert(builder: MethodSpec.Builder, value: String, result: String)

    fun getOriginType(): TypeName
    fun getConvertType(): TypeName

}