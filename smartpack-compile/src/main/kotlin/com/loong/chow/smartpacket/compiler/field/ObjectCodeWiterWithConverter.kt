package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class ObjectCodeWiterWithConverter(override val _type: TypeName, val covert: TypeConvertInterface) : ObjectCodeWiter(_type) {

    val writer by lazy {
        ObjectCodeWiter(covert.getConvertType())
    }

    override fun unpackObj(builder: MethodSpec.Builder, unPackbuilder: ParameterSpec, vtable: String, result: String) {


        builder.apply {
            addStatement("\$T ${result}_pending_to_revert=null", covert.getConvertType())
            writer.unpackObj(builder, unPackbuilder, vtable, "${result}_pending_to_revert")
            covert.revert(builder, "${result}_pending_to_revert", result)
        }

    }


    override fun packObject(builder: MethodSpec.Builder, packbuilder: ParameterSpec, value: String, offsetResult: String) {


        builder.apply {
            addStatement("\$T ${value}_pending_to_convert=null", covert.getConvertType())
            covert.convert(builder, value, "${value}_pending_to_convert")
            writer.packObject(builder, packbuilder, "${value}_pending_to_convert", offsetResult)
        }
    }
}