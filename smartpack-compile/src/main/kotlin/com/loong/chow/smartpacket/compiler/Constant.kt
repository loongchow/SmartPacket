package com.loong.chow.smartpacket.compiler

import com.google.auto.common.MoreTypes
import com.loong.chow.PackHelper
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

val PACK_HELPER: ClassName by lazy { ClassName.get(PackHelper::class.java) }
const val NEW_LINE = ";\r\n";

const val PACK_INTERFACE = "ParcelHelper"
fun packMethodName(enclosingElement: Element): String {
    return "pack${enclosingElement.simpleName}"
}

fun packMethodName(type: TypeMirror): String {
    return packMethodName(MoreTypes.asElement(type))
}

fun unPackMethodName(enclosingElement: Element): String {
    return "unPack${enclosingElement.simpleName}"
}

fun unPackMethodName(type: TypeMirror): String {
    return unPackMethodName(MoreTypes.asElement(type))
}

fun helper(_typeName: TypeName): TypeName {
    var clazzNameStr = (_typeName as ClassName).simpleName().toString()
    val packageName = (_typeName as ClassName).packageName().toString()
    val helperTypeName = ClassName.get(packageName, clazzNameStr + "Helper")
    return helperTypeName
}

fun packMethodName(className: ClassName): String {
    return "pack${className.simpleName()}"
}

fun unPackMethodName(className: ClassName): String {
    return "unPack${className.simpleName()}"
}

