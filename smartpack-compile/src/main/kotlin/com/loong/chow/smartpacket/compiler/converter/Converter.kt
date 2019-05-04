package com.loong.chow.smartpacket.compiler.converter

import com.google.common.collect.HashMultimap
import com.loong.chow.annotation.TypeConverters
import com.loong.chow.smartpacket.compiler.Context
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement

class Converter(type: TypeName, transformMethodName: String, reverseMethodName: String, expect: TypeName, transformType: TypeName) {


    val type = type
    val transformMethodName = transformMethodName
    val reverseMethodName = reverseMethodName
    val transformType = transformType
    val orginType = expect;

    companion object {
        val TypeConvertersMap = HashMultimap.create<TypeName, Converter>()

        val VALUE_ELEMENT by lazy {
            val typeElement = Context.elementUtils.getTypeElement(TypeConverters::class.java.canonicalName)
            var valueElement = typeElement.enclosedElements.first {
                var find = false;
                if (it is ExecutableElement) {
                    find = it.simpleName.toString() == "value"
                }
                find
            }
            valueElement
        }
    }


}