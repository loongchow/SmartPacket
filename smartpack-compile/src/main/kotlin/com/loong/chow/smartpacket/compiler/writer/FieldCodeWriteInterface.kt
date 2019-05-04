package com.loong.chow.smartpacket.compiler.writer

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

interface FieldCodeWriteInterface {
    fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String?
    fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int)
    fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_Offset: Int, result: String): String
    fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String)
    fun getTypeName(): TypeName
    fun needGenerateOffset(): Boolean
}