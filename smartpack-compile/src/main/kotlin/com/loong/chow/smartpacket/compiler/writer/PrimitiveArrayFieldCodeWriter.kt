package com.loong.chow.smartpacket.compiler.writer

import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class PrimitiveArrayFieldCodeWriter(val _typeName: TypeName) : FieldCodeWriteInterface {

    val cmpTypeName by lazy {
        assert(_typeName is ArrayTypeName)
        (_typeName as ArrayTypeName).componentType
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String? {

        builder.addStatement("$offsetName=\$N.${packMethodName()}($value)", packBuilder)
        return offsetName;
    }

    override fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int) {

        throw IllegalAccessError("cant access this method ")
    }

    override fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_Offset: Int, result: String): String {

        builder.addStatement("$result=\$N.${unpackMethodName()}($vtable_Offset)", unpackBuilder)
        return result
    }

    override fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String) {

        builder.addStatement("$result=\$N.${unpackMethodName()}ByLocation($vtable)", unpackbuilder)
    }

    fun unpackMethodName(): String {
        return when (cmpTypeName) {
            TypeName.BOOLEAN -> "pickBooleanArray"
            TypeName.BYTE -> "pickByteArray"
            TypeName.SHORT -> "pickShortArray"
            TypeName.INT -> "pickIntArray"
            TypeName.LONG -> "pickLongArray"
            TypeName.FLOAT -> "pickFloatArray"
            TypeName.DOUBLE -> "pickDoubleArray"
            else -> error("类型错误")
        }
    }

    fun packMethodName(): String {

        return when (cmpTypeName) {
            TypeName.BOOLEAN -> "addBooleanArray"
            TypeName.BYTE -> "addByteArray"
            TypeName.SHORT -> "addShortArray"
            TypeName.INT -> "addIntArray"
            TypeName.LONG -> "addLongArray"
            TypeName.FLOAT -> "addFloatArray"
            TypeName.DOUBLE -> "addDoubleArray"
            else -> error("类型错误")

        }

    }

    override fun getTypeName(): TypeName {
        return _typeName

    }

    override fun needGenerateOffset(): Boolean {
        return true
    }
}