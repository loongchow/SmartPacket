package com.loong.chow.smartpacket.compiler.field

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class UnKownSnapCodeDef(open val id: Int, open val fieldOwner: String?, open val fieldName: String, open val fieldType: TypeName, open val offsetName: String, open val unpackHelper: ParameterSpec,
                        open val packHelper: ParameterSpec) : FieldCodeGenerate {
    override fun packFieldDirect(builder: MethodSpec.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun needOffset(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generatePackCode(builder: MethodSpec.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValue(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setValue(builder: MethodSpec.Builder, value: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initOffsetName(builder: MethodSpec.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}