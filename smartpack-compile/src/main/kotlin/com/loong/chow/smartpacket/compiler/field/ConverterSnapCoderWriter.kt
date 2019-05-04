package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.loong.chow.smartpacket.compiler.initValue
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class ConverterSnapCoderWriter(id: Int, fieldOwner: String?, fieldName: String, fieldType: TypeName, offsetName: String, unpackHelper: ParameterSpec, packHelper: ParameterSpec, val convertInterface: TypeConvertInterface)
    : SnapCodeDef(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper) {


    val snapCodeDef by lazy {
        SnapCodeDefWithConvert(id, null, getConvertResult(), convertInterface.getConvertType(), offsetName, unpackHelper, packHelper, mutableSetOf())
    }

    init {


    }

    fun getConvertResult(): String {
        val result = "${tempName()}_convert_result"
        return result;
    }

    fun getRevertResult(): String {
        val result = "${tempName()}_revert_result"
        return result;
    }

    fun initConvertResult(builder: MethodSpec.Builder): String {
        val result = getConvertResult()
        builder.addStatement("\$T $result=${initValue(convertInterface!!.getConvertType())}", convertInterface!!.getConvertType())
        return result
    }


    fun initRevertResult(builder: MethodSpec.Builder): String {
        val result = getRevertResult()
        builder.addStatement("\$T $result=${initValue(convertInterface!!.getOriginType())}", convertInterface!!.getOriginType())
        return result
    }


    override fun generateOffsetCode(builder: MethodSpec.Builder) {
        initConvertResult(builder)
        convert(builder, getValue(), getConvertResult())
        snapCodeDef.generateOffsetCode(builder)
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        initRevertResult(builder)
        initConvertResult(builder)
        snapCodeDef.generateUnpackCode(builder)
        revert(builder, getConvertResult(), getRevertResult())

        setValue(builder, getRevertResult())
    }

    fun convert(builder: MethodSpec.Builder, value: String, result: String) {
        convertInterface.convert(builder, value, result)
    }

    fun revert(builder: MethodSpec.Builder, value: String, result: String) {
        convertInterface.revert(builder, value, result)
    }

}