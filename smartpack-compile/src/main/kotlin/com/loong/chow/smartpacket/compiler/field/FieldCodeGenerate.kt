package com.loong.chow.smartpacket.compiler.field

import com.squareup.javapoet.MethodSpec

interface FieldCodeGenerate {
    fun generateUnpackCode(builder: MethodSpec.Builder)
    fun generatePackCode(builder: MethodSpec.Builder)
    fun generateOffsetCode(builder: MethodSpec.Builder)
    fun packFieldDirect(builder: MethodSpec.Builder)
    fun getValue(): String
    fun setValue(builder: MethodSpec.Builder, value: String)
    fun initOffsetName(builder: MethodSpec.Builder)
    fun needOffset(): Boolean


}