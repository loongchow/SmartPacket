package com.loong.chow.smartpacket.compiler

import com.loong.chow.smartpacket.compiler.writer.*
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

class FieldCodeWriteInterfaceFactory {
    companion object {
        fun getFieldCodeWriteInterface(type: TypeName): FieldCodeWriteInterface? {
            return when {
                type.isPrimitive -> PrimitiveFieldCodeWriter(type)
                type is ArrayTypeName && type.componentType.isPrimitive -> PrimitiveArrayFieldCodeWriter(type)
                type.isBoxedPrimitive -> BoxPrimitveCodeWriter(type);
                StringFieldCodeWriter.isString(type) -> StringFieldCodeWriter(type)
                CustomeFieldCodeWiter.support(type) -> CustomeFieldCodeWiter(type)
                else -> null
            };
        }

        fun isCustomeList(type: TypeName): Boolean {
            if (type is ParameterizedTypeName) {
                if (type.rawType == ClassName.get(List::class.java) || type.rawType == ClassName.get(ArrayList::class.java)) {
                    val compType = type.typeArguments[0];
                    if (compType is ClassName) {
                        return true
                    }
                    return false
                }
                return false
            }
            return false
        }
    }


}