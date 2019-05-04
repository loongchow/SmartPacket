package com.loong.chow.smartpacket.compiler.writer

import com.squareup.javapoet.*

class StringFieldCodeWriter(val _typeName: TypeName) : FieldCodeWriteInterface {
    override fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String) {

        builder.addStatement("$result=\$N.pickStringByLocation($vtable)", unpackbuilder)
    }


    override fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String? {
        builder.addStatement("$offsetName=\$N.${packMethod()}($value)", packBuilder)
        return offsetName;
    }

    override fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int) {

        //do nothing
    }

    override fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_offset: Int, result: String): String {

        builder.addStatement("$result=\$N.${unPakcMethod()}($vtable_offset)", unpackBuilder)
        return result;
    }


    fun packMethod(): String {
        return "createString"
    }

    fun unPakcMethod(): String {
        return "pickString"

    }


    override fun getTypeName(): TypeName {
        return _typeName
    }

    override fun needGenerateOffset(): Boolean {
        return true
    }

    companion object {

        fun isString(typeName: TypeName): Boolean {
            if (typeName.withoutAnnotations() == ClassName.get(String::class.java)) {
                return true
            }
            return false
        }

        fun isStringList(typeName: TypeName): Boolean {
            if (typeName.withoutAnnotations() == ParameterizedTypeName.get(List::class.java, String::class.java)) {
                return true
            } else if (typeName.withoutAnnotations() == ParameterizedTypeName.get(ArrayList::class.java, String::class.java)) {
                return true
            }
            return false
        }

        fun support(typeName: TypeName): Boolean {
            return isString(typeName)
        }
    }
}