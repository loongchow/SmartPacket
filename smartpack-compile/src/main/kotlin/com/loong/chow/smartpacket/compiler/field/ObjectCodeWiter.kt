package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.FieldCodeWriteInterfaceFactory
import com.loong.chow.smartpacket.compiler.Log
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

open class ObjectCodeWiter(open val _type: TypeName) : ObjectCodeWriteInterface {

    var logger: Log? = null
    val fieldCodeWriteInterface by lazy {
        var temp = FieldCodeWriteInterfaceFactory.getFieldCodeWriteInterface(_type);
        if (temp == null) {
            logger?.e("can not find right FieldCodeWriteInterface")
        }
        temp!!

    }

    override fun unpackObj(builder: MethodSpec.Builder, unPackbuilder: ParameterSpec, vtable: String, result: String) {

        fieldCodeWriteInterface!!.unpackObj(builder, unPackbuilder, vtable, result)


    }

    override fun packObject(builder: MethodSpec.Builder, packbuilder: ParameterSpec, value: String, offsetResult: String) {

        fieldCodeWriteInterface!!.generateOffsetCode(builder, packbuilder, value, offsetResult)
    }
}