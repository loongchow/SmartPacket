package com.loong.chow.smartpacket.compiler.field

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec

interface ObjectCodeWriteInterface {
    fun unpackObj(builder: MethodSpec.Builder, unPackbuilder: ParameterSpec, vtable: String, result: String)
    fun packObject(builder: MethodSpec.Builder, packbuilder: ParameterSpec, value: String, offsetResult: String);
}