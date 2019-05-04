package com.loong.chow.smartpacket.compiler.writer

import com.loong.chow.smartpacket.compiler.initValue
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class PrimitiveFieldCodeWriter(val _typeName: TypeName) : FieldCodeWriteInterface {
    override fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String) {

//        unpack(builder,unpackbuilder)

        throw  IllegalAccessError("cannot call this method")
    }


    override fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String? {
        return null
    }

    override fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int) {

        builder.addStatement("\$N.${packMethodName()}($id,$value,${initValue(_typeName)})", packBuilder)
    }

    override fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_offset: Int, result: String): String {

        builder.addStatement("$result=\$N.${unpackMethodName()}($vtable_offset)", unpackBuilder)
        return result
    }

    fun unpackMethodName(): String {
        return when (_typeName) {
            TypeName.BOOLEAN -> "pickBoolean"
            TypeName.BYTE -> "pickByte"
            TypeName.SHORT -> "pickShort"
            TypeName.INT -> "pickInt"
            TypeName.LONG -> "pickLong"
            TypeName.FLOAT -> "pickFloat"
            TypeName.DOUBLE -> "pickDouble"
            else -> error("类型错误")
        }
    }

    fun packMethodName(): String {

        return when (_typeName) {
            TypeName.BOOLEAN -> "addBoolean"
            TypeName.BYTE -> "addByte"
            TypeName.SHORT -> "addShort"
            TypeName.INT -> "addInt"
            TypeName.LONG -> "addLong"
            TypeName.FLOAT -> "addFloat"
            TypeName.DOUBLE -> "addDouble"
            else -> error("类型错误")

        }


    }

    override fun getTypeName(): TypeName {
        return _typeName
    }

    override fun needGenerateOffset(): Boolean {
        return false
    }
}