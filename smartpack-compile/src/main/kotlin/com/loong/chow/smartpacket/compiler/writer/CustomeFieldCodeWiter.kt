package com.loong.chow.smartpacket.compiler.writer

import com.loong.chow.UnpackHelper
import com.loong.chow.smartpacket.compiler.helper
import com.loong.chow.smartpacket.compiler.packMethodName
import com.loong.chow.smartpacket.compiler.unPackMethodName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

public class CustomeFieldCodeWiter(val _typeName: TypeName) : FieldCodeWriteInterface {
    override fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String) {

        builder.apply {
            beginControlFlow("if($vtable>0)")
            addStatement("\$T ${result}_unPackHelper=new \$T(\$N,$vtable)", UnpackHelper::class.java, UnpackHelper::class.java, unpackbuilder)
            addStatement("$result=\$T.${unPackMethodName()}(${result}_unPackHelper)", helper(_typeName))
            endControlFlow()
        }
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String? {


        builder.addStatement("$offsetName=\$T.${packMethodName()}(\$N,$value)", helper(_typeName), packBuilder)

        return offsetName;

    }

    override fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int) {

        //do nothing
    }

    override fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_offset: Int, result: String): String {

        builder.apply {
            addStatement("int ${result}_position=\$N.__indirect($vtable_offset)", unpackBuilder)

            unpackObj(builder, unpackBuilder, "${result}_position", result)
        }
        return result;

    }


    fun packMethodName(): String {
        return packMethodName(_typeName as ClassName)
    }

    fun unPackMethodName(): String {
        return unPackMethodName(_typeName as ClassName)
    }

    override fun getTypeName(): TypeName {
        return _typeName
    }

    override fun needGenerateOffset(): Boolean {
        return true;
    }

    companion object {
        fun support(typeName: TypeName): Boolean {
            return typeName is ClassName
        }
    }
}