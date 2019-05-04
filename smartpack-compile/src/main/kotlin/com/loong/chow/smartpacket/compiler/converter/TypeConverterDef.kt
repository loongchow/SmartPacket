package com.loong.chow.smartpacket.compiler.converter

import com.squareup.javapoet.MethodSpec.Builder
import com.squareup.javapoet.TypeName

class TypeConverterDef(private val _convertType: TypeName, private val _originType: TypeName, val revertMethodName: String, val convertMethodName: String, val convertClassName: TypeName)
    : TypeConvertInterface {


    override fun getOriginType(): TypeName {
        return _originType
    }

    override fun getConvertType(): TypeName {
        return _convertType;
    }

    override fun convert(builder: Builder, value: String, result: String) {

        builder.addStatement("$result=\$T.$convertMethodName($value)", convertClassName)
    }

    override fun revert(builder: Builder, value: String, result: String) {
        builder.addStatement("$result=\$T.$revertMethodName($value)", convertClassName)
    }


}