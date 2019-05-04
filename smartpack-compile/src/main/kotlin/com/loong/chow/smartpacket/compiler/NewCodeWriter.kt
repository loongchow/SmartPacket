package com.loong.chow.smartpacket.compiler

import com.google.auto.common.MoreElements
import com.google.auto.common.MoreTypes
import com.loong.chow.PackHelper
import com.loong.chow.UnpackHelper
import com.loong.chow.annotation.PackFlag
import com.loong.chow.annotation.TypeConverters
import com.loong.chow.smartpacket.compiler.converter.Converter
import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.loong.chow.smartpacket.compiler.converter.TypeConverterDef
import com.loong.chow.smartpacket.compiler.field.FieldCodeGenerate
import com.loong.chow.smartpacket.compiler.field.SnapCodeDef
import com.loong.chow.smartpacket.compiler.field.SnapCodeDefWithConvert
import com.squareup.javapoet.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

class NewCodeWriter {

    fun generateCode(processingEnv: ProcessingEnvironment, enclosingElement: Element, elements: Set<Element>) {


        val type = enclosingElement.asType()
        val packageElement = processingEnv.elementUtils.getPackageOf(enclosingElement)
        val orgClassType = ClassName.get(type)

        val packHelperParam = ParameterSpec.builder(PackHelper::class.java, "packHelper").build()
        val objParameter = ParameterSpec.builder(orgClassType, "obj").build()


        val unpackHelper = ParameterSpec.builder(UnpackHelper::class.java, "unPackHelper").build()


        val pair = collectSnapCode(processingEnv, type, unpackHelper, packHelperParam, elements, enclosingElement)


        val packMethodBuilder = packMehtodBuilder(enclosingElement, packHelperParam, objParameter, pair);
        val unPackMethodBuilder = unpackBuilder(enclosingElement, orgClassType, unpackHelper, objParameter, pair);


        val packMethodSpec = packMethodBuilder.build()

        /**
         * public static final Sample unPackSample(unpackHelper,obj)
         */
        val unPackMethodSpec = unPackMethodBuilder.build()

        /**
         * publict static Sample unpackSample(unpackHelper)
         */

        val unpackMethodBuilder2 = MethodSpec.methodBuilder(unPackMethodName(enclosingElement))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(unpackHelper)
                .returns(orgClassType)
                .beginControlFlow("if(\$N.invalid())", unpackHelper)
                .addStatement("return null")
                .endControlFlow()
                .addStatement(" \$T obj=new \$T()", orgClassType, orgClassType)
                .addStatement("return \$N(\$N,obj)", unPackMethodSpec, unpackHelper)


        /**
         * public static final byte[] toBytes(obj)
         */
        val toBytesBuilder = MethodSpec.methodBuilder("toBytes")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(objParameter).returns(ByteArray::class.java)
                .addStatement("\$T packHelper=new \$T()", PackHelper::class.java, PackHelper::class.java)
                .addStatement("int offset= \$N(packHelper,\$N)", packMethodSpec, objParameter)
                .addStatement("packHelper.finish(offset)")
                .addStatement("return packHelper.sizedByteArray()")

        val toBytesMethod = toBytesBuilder.build()

        /**
         * public static Sample fromBytes(byte[] bytes)
         */
        val fromBytesBuilder = MethodSpec.methodBuilder("fromBytes")
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ByteArray::class.java, "bytes").returns(orgClassType)
                .addStatement("\$T _bb=ByteBuffer.wrap(bytes)", ByteBuffer::class.java)
                .addStatement(" _bb.order(\$T.LITTLE_ENDIAN)", ClassName.get(ByteOrder::class.java))
                .addStatement("int bb_pos=_bb.getInt(_bb.position()) + _bb.position()")
                .addStatement("\$T packHelper =new \$T(_bb,bb_pos)", UnpackHelper::class.java, UnpackHelper::class.java)
                .addStatement("return \$N(packHelper)", unPackMethodSpec)


        val fromBytesMethod = fromBytesBuilder.build()


        val className = "${enclosingElement.simpleName}Helper"


        val packageName = packageElement.qualifiedName.toString()


        val builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)

        builder.apply {
            addMethod(toBytesMethod)
            addMethod(fromBytesMethod)
            addMethod(packMethodSpec)
            addMethod(unPackMethodSpec)
            addMethod(unpackMethodBuilder2.build())
            addJavadoc(CodeBlock.of("generate by autopack ,do not modify  manually\r\n," +
                    "create time:${java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).format(Date())}\r\n"))


        }
        val javaFile = JavaFile.builder(packageName, builder.build()).build()
        javaFile.writeTo(processingEnv.filer)


    }

    /**
     * public static final Sample unPackSample(unpackHelper,obj)
     */
    private fun unpackBuilder(enclosingElement: Element, orgClassType: TypeName?, unpackHelper: ParameterSpec?, objParameter: ParameterSpec?, pair: Pair<LinkedList<FieldCodeGenerate>, Int>): MethodSpec.Builder {
        val unPackMethodBuilder = MethodSpec.methodBuilder(unPackMethodName(enclosingElement))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(orgClassType)
                .addParameter(unpackHelper)
                .addParameter(objParameter)

        unPackMethodBuilder
                .beginControlFlow("if(unPackHelper==null)")
                .addStatement("return obj")
                .endControlFlow()
                .beginControlFlow("if(obj==null)")
                .addStatement("obj=new \$T()", orgClassType)
                .endControlFlow()

        for (code in pair.first) {
            code.generateUnpackCode(unPackMethodBuilder)
        }
        unPackMethodBuilder.addStatement("return obj")
        return unPackMethodBuilder
    }

    /**
     * int packSample(packHelper,sample obj)
     */
    private fun packMehtodBuilder(enclosingElement: Element, packHelperParam: ParameterSpec?, objParameter: ParameterSpec?, pair: Pair<LinkedList<FieldCodeGenerate>, Int>): MethodSpec.Builder {
        val packMethodBuilder = MethodSpec.methodBuilder(packMethodName(enclosingElement))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .returns(TypeName.INT)
                .addParameter(packHelperParam)
                .addParameter(objParameter)
        packMethodBuilder.beginControlFlow("if(obj==null)")
                .addStatement("return 0")
                .endControlFlow()
        packMethodBuilder.apply {
            for (code in pair.first) {
                code.initOffsetName(this)
                code.generateOffsetCode(this)
            }
            addStatement("\$N.startObject(\$L)", packHelperParam, pair.second + 1)
            for (code in pair.first) {
                code.generatePackCode(this)
            }
            addStatement("return packHelper.endObject()")
        }
        return packMethodBuilder
    }

    private fun collectSnapCode(processingEnv: ProcessingEnvironment, type: TypeMirror?, unpackHelper: ParameterSpec, packHelperParam: ParameterSpec, elements: Set<Element>, enclosingElement: Element): Pair<LinkedList<FieldCodeGenerate>, Int> {
        val declaredTypeOption = MoreTypes.nonObjectSuperclass(processingEnv.typeUtils, processingEnv.elementUtils, type as DeclaredType)

        var packParent = false


        val queue = LinkedList<FieldCodeGenerate>()

//
//
//        if (declaredTypeOption.isPresent) {
//            val declaredType = declaredTypeOption.get()
//            val parentElement = declaredType.asElement()
//            parentElement.enclosedElements.filter {
//                it.getAnnotation(PackFlag::class.java) != null
//            }.apply {
//                if (isNotEmpty()) {
//                    packParent = true
//                    var log = JavacLog(parentElement)
//                    val snapCodeDef = SnapCodeDef(0, null, "__parentFiled", ClassName.get(parentElement.asType()), "__parentFiled_offset", unpackHelper, packHelperParam).apply {
//                        this.logger = log;
//                    }
//                    queue.add(snapCodeDef)
//                }
//            }
//
//
//        }


        var maxId = if (packParent) 0 else -1

        for (element in elements) {

            var log = JavacLog(element)

            val annotationMirror = when {
                MoreElements.isAnnotationPresent(element, TypeConverters::class.java) -> MoreElements.getAnnotationMirror(element, TypeConverters::class.java).get()
                MoreElements.isAnnotationPresent(enclosingElement, TypeConverters::class.java) -> MoreElements.getAnnotationMirror(enclosingElement, TypeConverters::class.java).get()
                else -> null
            }

            val typeWithAnnotation = if (annotationMirror == null) {
                TypeName.get(element.asType())
            } else {
                TypeName.get(element.asType()).annotated(AnnotationSpec.get(annotationMirror))
            }

            var convertInterfaces: MutableSet<TypeConvertInterface> = mutableSetOf();

            Converter.TypeConvertersMap[typeWithAnnotation]?.forEach {
                convertInterfaces.add(TypeConverterDef(it.transformType, it.orginType, it.reverseMethodName, it.transformMethodName, it.type))
            }

            var index = element.getAnnotation(PackFlag::class.java).value
            index = if (packParent) index + 1 else index

            if (index > maxId) {
                maxId = index;
            }
            val snap = SnapCodeDefWithConvert(index, "obj", element.simpleName.toString(), typeWithAnnotation, offsetName("_obj", element.simpleName.toString()), unpackHelper, packHelperParam, convertInterfaces).apply {
                this.logger = log;
            }


            queue.add(snap)


        }
        return Pair(queue, maxId)
    }

    final fun offsetName(fieldOwner: String, fieldName: String): String {
        return "_${fieldOwner}_${fieldName}_offset"
    }

}