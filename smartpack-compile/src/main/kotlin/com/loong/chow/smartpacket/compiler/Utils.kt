package com.loong.chow.smartpacket.compiler

import com.loong.chow.annotation.PackFlag
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.util.ArrayList
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import kotlin.collections.HashMap

fun isAutoPackType(type: TypeName, elements: Elements): Boolean {
    return when {
        isAutoPackBaseType(type, elements) -> true
        type is ArrayTypeName -> when {
            isAutoPackBaseType(type.componentType, elements) -> true
            else -> {
                false
            }
        }
        type is ParameterizedTypeName -> when {
            type.rawType == ClassName.get(List::class.java) -> type.typeArguments.all { isAutoPackBaseType(it, elements) }
            type.rawType == ClassName.get(ArrayList::class.java) -> type.typeArguments.all { isAutoPackBaseType(it, elements) }
            type.rawType == ClassName.get(Map::class.java) -> type.typeArguments.all { isAutoPackBaseType(it, elements) }
            type.rawType == ClassName.get(HashMap::class.java) -> type.typeArguments.all { isAutoPackBaseType(it, elements) }
            else -> {
                return false
            }
        }
        else -> {
            false
        }
    }

}

fun isListType(type: TypeName, elements: Elements): Boolean {
    return when (type) {
        is ParameterizedTypeName -> when {
            type.rawType == ClassName.get(List::class.java) -> type.typeArguments.all { isAutoPackBaseType(type, elements) }
            type.rawType == ClassName.get(ArrayList::class.java) -> type.typeArguments.all { isAutoPackBaseType(type, elements) }
            else -> {
                false
            }
        }
        else -> {
            false;
        }
    }
}

fun isMapType(type: TypeName, elements: Elements): Boolean {
    return when (type) {
        is ParameterizedTypeName -> when {
            type.rawType == ClassName.get(Map::class.java) -> type.typeArguments.all { isAutoPackBaseType(type, elements) }
            type.rawType == ClassName.get(HashMap::class.java) -> type.typeArguments.all { isAutoPackBaseType(type, elements) }
            else -> {
                false
            }
        }
        else -> {
            false;
        }
    }
}

fun isAutoPackBaseType(type: TypeName, elements: Elements): Boolean {
    return when {
        type.isPrimitive -> return true
        type.isBoxedPrimitive -> return true
        type == ClassName.get(String::class.java) -> return true
        isAutoPackCustomeType(type, elements) -> {
            return true
        }
        type is ArrayTypeName && type.componentType.isPrimitive -> false
        else -> {
            false
        }
    }
}

fun isAutoPackCustomeType(type: TypeName, elements: Elements): Boolean {
    return when (type) {
        is ClassName -> {
            val element: TypeElement = elements.getTypeElement(type.peerClass(type.simpleName()).toString())
            return if (element != null) {
                return element.enclosedElements.any {
                    it.getAnnotation(PackFlag::class.java) != null
                }
            } else {
                false
            }
        }
        else -> {
            false
        }
    }


}

fun initValue(typeName: TypeName): String {
    return when {
        typeName == TypeName.BOOLEAN -> "false"
        typeName.isPrimitive -> "0"
        else -> "null"
    }

}