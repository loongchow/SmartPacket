package com.loong.chow.smartpacket.compiler.writer

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

class BoxPrimitveCodeWriter(val _typeName: TypeName) : FieldCodeWriteInterface {
    override fun generateOffsetCode(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, offsetName: String): String? {

        /**
         * int offset = Byte.createByte(this, val);
         */

        builder.addStatement("$offsetName=\$T.${create()}(\$N,$value)", packType(), packBuilder)
        return offsetName

    }

    override fun packFieldDirect(builder: MethodSpec.Builder, packBuilder: ParameterSpec, value: String, id: Int) {


        builder.addStatement("\$N.${packMethodName()}($id,$value)", packBuilder)
    }

    override fun unpack(builder: MethodSpec.Builder, unpackBuilder: ParameterSpec, vtable_Offset: Int, result: String): String {

        builder.addStatement("$result=\$N.${unPackMethodName()}($vtable_Offset)", unpackBuilder)
        return result
    }

    override fun unpackObj(builder: MethodSpec.Builder, unpackbuilder: ParameterSpec, vtable: String, result: String) {


        builder.apply {
            addStatement("\$T ${result}_temp=new \$T()", packType(), packType())
            addStatement("${result}_temp.__assign($vtable,\$N.getBb())", unpackbuilder)
            addStatement("$result=new \$T(${result}_temp.val())", _typeName)
        }
        /**
         *  Boolean b = new Boolean();
        b.__assign(bb_pos + o, bb);
        return new java.lang.Boolean(b.val());
         */
    }

    fun packMethodName(): String {
        return when (_typeName.unbox().withoutAnnotations()) {

            TypeName.BOOLEAN -> "addBoxedBoolean"
            TypeName.BYTE -> "addBoxedByte"
            TypeName.SHORT -> "addBoxedShort"
            TypeName.INT -> "addBoxedInt"
            TypeName.LONG -> "addBoxedLong"
            TypeName.DOUBLE -> "addBoxedDouble"
            TypeName.FLOAT -> "addBoxedFloat"
            else -> "unkownName"
        }
    }

    fun unPackMethodName(): String {
        return when (_typeName.unbox().withoutAnnotations()) {

            TypeName.BOOLEAN -> "pickBoxedBoolean"
            TypeName.BYTE -> "pickBoxedByte"
            TypeName.SHORT -> "pickBoxedShort"
            TypeName.INT -> "pickBoxedInt"
            TypeName.LONG -> "pickBoxedLong"
            TypeName.DOUBLE -> "pickBoxedDouble"
            TypeName.FLOAT -> "pickBoxedFloat"
            else -> "unkownName"
        }
    }


    private fun create(): String {
        return when (_typeName.unbox().withoutAnnotations()) {

            TypeName.BOOLEAN -> "createBoolean"
            TypeName.BYTE -> "createByte"
            TypeName.SHORT -> "createShort"
            TypeName.INT -> "createInt"
            TypeName.LONG -> "createLong"
            TypeName.DOUBLE -> "createDouble"
            TypeName.FLOAT -> "createFloat"
            else -> "unkownName"
        }
    }

    private fun packType(): ClassName {
        return when (_typeName.unbox().withoutAnnotations()) {

            TypeName.BOOLEAN -> ClassName.get(com.loong.chow.type.Boolean::class.java)
            TypeName.BYTE -> ClassName.get(com.loong.chow.type.Byte::class.java)
            TypeName.SHORT -> ClassName.get(com.loong.chow.type.Short::class.java)
            TypeName.INT -> ClassName.get(com.loong.chow.type.Int::class.java)
            TypeName.LONG -> ClassName.get(com.loong.chow.type.Long::class.java)
            TypeName.DOUBLE -> ClassName.get(com.loong.chow.type.Double::class.java)
            TypeName.FLOAT -> ClassName.get(com.loong.chow.type.Float::class.java)
            else -> error("")
        }
    }

    override fun getTypeName(): TypeName {
        return _typeName

    }

    override fun needGenerateOffset(): Boolean {
        return false;
    }
}