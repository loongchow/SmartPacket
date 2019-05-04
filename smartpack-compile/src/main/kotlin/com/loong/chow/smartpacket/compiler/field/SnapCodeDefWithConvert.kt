package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.squareup.javapoet.*

open class SnapCodeDefWithConvert(override val id: Int, override val fieldOwner: String?, override val fieldName: String, override val fieldType: TypeName, override val offsetName: String, override val unpackHelper: ParameterSpec,
                                  override val packHelper: ParameterSpec, open val converts: MutableSet<TypeConvertInterface>) : SnapCodeDef(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper) {


    open val convert by lazy {

        converts.firstOrNull {
            it.getOriginType().withoutAnnotations() == fieldType.withoutAnnotations()
        }
    }


    val realSnapCodeDef: FieldCodeGenerate by lazy<FieldCodeGenerate> {

        if (convert != null) {
            ConverterSnapCoderWriter(this.id, this.fieldOwner, this.fieldName, this.fieldType, this.offsetName, unpackHelper, packHelper, this.convert!!)
        } else {


            if (fieldType is ArrayTypeName) {
                if ((fieldType as ArrayTypeName).componentType.isPrimitive) {

                    SnapCodeDef(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper)
                } else {
                    ArrayRefCodeWithConvert(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts)
                }

            } else if (fieldType is ParameterizedTypeName) {
                val rawType = (fieldType as ParameterizedTypeName).rawType;
                if (rawType == ClassName.get(List::class.java) || rawType == ClassName.get(ArrayList::class.java)) {
                    ListCodeDefWithConvert(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts)
                } else if (rawType == ClassName.get(HashMap::class.java) || rawType == ClassName.get(Map::class.java)) {
                    MapCodeSnapDef(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts)
                } else {
                    error("not support this type")
                    UnKownSnapCodeDef(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper);
                }

            } else {
                SnapCodeDef(id, fieldOwner, fieldName, fieldType, this.offsetName, unpackHelper, packHelper)
            }


        }


    }


    override fun generateOffsetCode(builder: MethodSpec.Builder) {
        realSnapCodeDef.generateOffsetCode(builder)
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        realSnapCodeDef.generateUnpackCode(builder)
    }


    override fun packFieldDirect(builder: MethodSpec.Builder) {
        realSnapCodeDef.packFieldDirect(builder)
    }

    override fun needGenerateOffset(): Boolean {
        return realSnapCodeDef.needOffset()
    }


}