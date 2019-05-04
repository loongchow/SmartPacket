package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.FieldCodeWriteInterfaceFactory
import com.loong.chow.smartpacket.compiler.Log
import com.loong.chow.smartpacket.compiler.initValue
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName

open class SnapCodeDef(open val id: Int, open val fieldOwner: String?, open val fieldName: String, open val fieldType: TypeName, open val offsetName: String, open val unpackHelper: ParameterSpec,
                       open val packHelper: ParameterSpec) : FieldCodeGenerate {


    var logger: Log? = null;
    private val writer by lazy {
        val tmp = FieldCodeWriteInterfaceFactory.getFieldCodeWriteInterface(fieldType)
        if (tmp == null) {
            error("can not find FieldCodeWriteInterface")
        }
        tmp!!
    }


    constructor(def: SnapCodeDef) : this(def.id, def.fieldOwner, def.fieldName, def.fieldType, def.offsetName, def.unpackHelper, def.packHelper)


    fun error(msg: String) {

        logger?.e(msg)
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        initUnpackResult(builder)
        unpack(builder, unPackResult())
        var result = unPackResult()
        setValue(builder, result)
    }

    final override fun generatePackCode(builder: MethodSpec.Builder) {
        if (needGenerateOffset()) {
            builder.addStatement("\$N.addOffset($id,\$L,0)", this.packHelper, offsetName)
        } else {
            packFieldDirect(builder)
        }

        println("generatePackCode $fieldOwner.$fieldName needOffset:$needOffset")
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder) {

        if (needGenerateOffset()) {
            var value = getValue();
            generateOffsetCode(builder, value, offsetName)

        }
    }

    override open fun packFieldDirect(builder: MethodSpec.Builder) {

        var value = getValue();
        writer.packFieldDirect(builder, this.packHelper, value, id)
    }

    /**
     *  返回生成的返回生成的offset 的code，如果不需要offset则返回null
     */
    final fun generateOffsetCode(builder: MethodSpec.Builder, value: String, offsetName: String): String? {
        return writer.generateOffsetCode(builder, this.packHelper, value, offsetName)
    }

    final fun offsetName(): String {
//        return if(fieldOwner==null) {
//            "_${fieldName}_offset"
//        }
//        else{
//            "_${fieldOwner}_${fieldName}_offset"
//        }

        return offsetName;
    }

    open fun needGenerateOffset(): Boolean {
        return writer.needGenerateOffset();
    }

    final fun unpackMethodName(builder: MethodSpec.Builder, id: Int): String {
        initUnpackResult(builder)
        unpack(builder, unPackResult())
        return unPackResult();
    }

    final fun unpack(builder: MethodSpec.Builder, result: String) {

        writer.unpack(builder, unpackHelper, id * 2 + 4, result)
    }

    fun initUnpackResult(builder: MethodSpec.Builder): String {

        builder.addStatement("\$T ${unPackResult()}=${initValue(writer.getTypeName())}", writer.getTypeName())
        return unPackResult()
    }

    fun unPackResult(): String {

        return tempName()
    }

    override fun getValue(): String {

        return if (fieldOwner == null) {
            fieldName
        } else {
            "$fieldOwner.$fieldName"
        }
    }

    override fun setValue(builder: MethodSpec.Builder, value: String) {

        if (fieldOwner == null) {
            builder.addStatement("$fieldName=$value");
        } else {
            builder.addStatement("$fieldOwner.$fieldName=$value")

        }
    }

    open fun tempName(): String {
        return if (fieldOwner == null) {
            "_$fieldName"
        } else {
            "_${fieldOwner}_$fieldName"
        }
    }


    val needOffset = lazy {
        needGenerateOffset();
    };


    override fun initOffsetName(builder: MethodSpec.Builder) {
        builder.addStatement("int $offsetName=0")
    }

    override fun needOffset(): Boolean {
        return needOffset.value;
    }


}