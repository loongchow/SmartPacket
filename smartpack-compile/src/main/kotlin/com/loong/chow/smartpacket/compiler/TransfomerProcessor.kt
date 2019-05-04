package com.loong.chow.smartpacket.compiler

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap
import com.loong.chow.annotation.PackFlag
import com.loong.chow.annotation.TypeConverter
import com.loong.chow.annotation.TypeConverters
import com.loong.chow.smartpacket.compiler.converter.Converter
import com.loong.chow.smartpacket.compiler.converter.Converter.Companion.TypeConvertersMap
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.sun.tools.javac.code.Type
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import kotlin.collections.ArrayList

class TransfomerProcessor : BasicAnnotationProcessor() {


    var startTime = 0.toLong();
    var endTime = 0.toLong();
    var step = 0

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }


    override fun initSteps(): Iterable<BasicAnnotationProcessor.ProcessingStep> {

        Context.processingEnvironment = processingEnv;
        val steps = ArrayList<AutoPackProcessingStep>()
        steps.add(AutoPackProcessingStep())
        return steps
    }

    override fun postRound(roundEnv: RoundEnvironment?) {
        super.postRound(roundEnv)
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "${Date(System.currentTimeMillis())},第$step 轮处理完毕:prior root:${roundEnv?.rootElements}")
        step++
        if (roundEnv?.processingOver() == true) {
            endTime = System.currentTimeMillis()

            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "第$step 轮处理完毕,总共耗时${endTime - startTime}ms")
        }
    }

    internal inner class AutoPackProcessingStep : BasicAnnotationProcessor.ProcessingStep {


        init {
            startTime = System.currentTimeMillis()
        }

        override fun annotations(): Set<Class<out Annotation>> {
            val annotations = LinkedHashSet<Class<out Annotation>>()
            annotations.add(PackFlag::class.java)
            annotations.add(TypeConverter::class.java)
            annotations.add(TypeConverters::class.java)
            return annotations
        }

        val typeConverterMap = ArrayListMultimap.create<Element, ExecutableElement>()
        override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): Set<Element> {


            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "${Date(System.currentTimeMillis())} 第$step 轮处理开始处理")

            // 获取注解元素
            val packFlagSet = elementsByAnnotation.get(PackFlag::class.java)

            //获取类型转换逐渐元素
            val typeConverterSet = elementsByAnnotation.get(TypeConverter::class.java)

            for (convert in typeConverterSet) {
                typeConverterMap.put(convert.enclosingElement, convert as ExecutableElement?)
            }


            val typeConverters = elementsByAnnotation.get(TypeConverters::class.java)
            for (tmp in typeConverters) {
                val annotationMirror = MoreElements.getAnnotationMirror(tmp, TypeConverters::class.java).get();

                (annotationMirror.elementValues[Converter.VALUE_ELEMENT]?.value as List<com.sun.tools.javac.code.Attribute.Class>).all { clazz ->
                    var valid = (typeConverterMap[clazz.classType.asElement()].size >= 2)
                    if (!valid) {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "invalid TypeConverters,使用TypeConverter注解对应方法 ", tmp, annotationMirror, annotationMirror.elementValues[Converter.VALUE_ELEMENT])
                    }
                    valid
                }
            }

            var packFlagMap = LinkedHashMultimap.create<Element, Element>();




            for (element in packFlagSet) {

                var enclosingElement = element.enclosingElement
                packFlagMap.put(enclosingElement, element)
                val type = element.asType()

                if (!isAutoPackType(TypeName.get(type), processingEnv.elementUtils)) {
                    if (MoreElements.isAnnotationPresent(element, TypeConverters::class.java)) {
                        val annotationMirror = MoreElements.getAnnotationMirror(element, TypeConverters::class.java).get();
                        var found = processConverter(element, annotationMirror, type)
                        if (!found) {
                            if (type is DeclaredType) {
                                found = type.typeArguments?.all {
                                    isAutoPackBaseType(TypeName.get(it), processingEnv.elementUtils) or processConverter(element, annotationMirror, it)
                                } ?: false
                            } else if (type is Type.ArrayType) {
                                found = type.componentType?.let {
                                    isAutoPackBaseType(TypeName.get(it), processingEnv.elementUtils) or processConverter(element, annotationMirror, it)
                                } ?: false
                            }

                        }

                        if (!found) {
                            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "找不到匹配的类型,请请检查是否使用了@TypeConverter注解了对应方法，以及方法参数和返回类型是否匹配", element, annotationMirror)
                        }

                    } else if (MoreElements.isAnnotationPresent(enclosingElement, TypeConverters::class.java)) {
                        processConverter(element, MoreElements.getAnnotationMirror(enclosingElement, TypeConverters::class.java).get(), type)
                        if (type.kind == TypeKind.DECLARED) {
                            (type as DeclaredType).typeArguments?.forEach {
                                processConverter(element, MoreElements.getAnnotationMirror(enclosingElement, TypeConverters::class.java).get(), it)
                            }
                        }
                    } else {
                        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "你需要指定TypeConverter", element)
                    }
                }


            }

            if (packFlagMap.isEmpty) {
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "no more elements to process")
            } else {
                for (entry in packFlagMap.keySet()) {
                    NewCodeWriter().generateCode(processingEnv, entry, packFlagMap.get(entry))
                }
            }


            val builder = ImmutableSet.builder<Element>()
            return builder.build()
        }

        private fun processConverter(element: Element, annotationMirror: AnnotationMirror, expectType: TypeMirror): Boolean {

            var found = false;
            (annotationMirror.elementValues[Converter.VALUE_ELEMENT]?.value as List<com.sun.tools.javac.code.Attribute.Class>)
                    .firstOrNull { clazz ->


                        var transformType: TypeMirror? = null
                        var transform = typeConverterMap[clazz.classType.asElement()].firstOrNull { executableElement ->
                            TypeName.get(executableElement.parameters[0].asType()) == TypeName.get(expectType)
                        }


                        var reverse = transform?.let {
                            transformType = it.returnType
                            typeConverterMap[clazz.classType.asElement()].firstOrNull { executableElement ->
                                TypeName.get(executableElement.parameters[0].asType()) == TypeName.get(transformType) && TypeName.get(executableElement.returnType) == TypeName.get(expectType)
                            }
                        }

                        found = (transform != null) && (reverse != null) && transformType != null
                        if (found) {
                            var expectTypeName: TypeName? = when {
                                expectType.kind == TypeKind.ARRAY -> ArrayTypeName.get(expectType)
                                expectType.kind == TypeKind.DECLARED -> ClassName.get(expectType)
                                else -> null
                            }

                            var transformTypeName: TypeName? = when {
                                transformType!!.kind == TypeKind.ARRAY -> ArrayTypeName.get(transformType)
                                transformType!!.kind == TypeKind.DECLARED -> ClassName.get(transformType)
                                transformType!!.kind.isPrimitive -> TypeName.get(transformType);
                                else -> null
                            }

                            val typeName = TypeName.get(element.asType()).annotated(AnnotationSpec.get(annotationMirror))
                            System.out.println(typeName)
                            TypeConvertersMap.put(typeName, Converter(ClassName.get(clazz.classType), transform!!.simpleName.toString(), reverse!!.simpleName.toString(), expectTypeName!!, transformTypeName!!))

                        }
                        found
                    }
            return found;
        }


    }


}
