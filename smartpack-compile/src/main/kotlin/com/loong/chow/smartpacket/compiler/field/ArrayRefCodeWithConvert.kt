package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class ArrayRefCodeWithConvert(override val id: Int, override val fieldOwner: String?, override val fieldName: String, override val fieldType: TypeName, override val offsetName: String, override val unpackHelper: ParameterSpec, override val packHelper: ParameterSpec, override val converts: MutableSet<TypeConvertInterface>)
    : ListCodeDefWithConvert(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts) {

    override val comType by lazy {
        val comp = (fieldType as ArrayTypeName).componentType
        comp
    }

    override fun getLength(): String {
        return "${getValue()}.length"
    }

    override fun getValue(i: String): String {
        return "${getValue()}[$i]"
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        super.generateUnpackCode(builder)
    }

    override fun setIndexValue(builder: MethodSpec.Builder, index: String, item: String) {
        builder.addStatement("${getValue()}[$index]=$item")
    }

    override fun initResult(builder: MethodSpec.Builder, size: String) {
        builder.addStatement("\$T ${fieldName}_tmp=new \$T[$size]", fieldType, comType)
        setValue(builder, "${fieldName}_tmp")
    }
}