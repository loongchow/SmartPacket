package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.NEW_LINE
import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.loong.chow.type.Pair
import com.squareup.javapoet.*

class MapCodeSnapDef(id: Int, fieldOwner: String?, fieldName: String, fieldType: TypeName, offsetName: String, unpackHelper: ParameterSpec, packHelper: ParameterSpec, converts: MutableSet<TypeConvertInterface>)
    : SnapCodeDefWithConvert(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts) {


    val keyType by lazy {
        (fieldType as ParameterizedTypeName).typeArguments[0]
    }

    val valueType by lazy {
        (fieldType as ParameterizedTypeName).typeArguments[1]
    }

    val keyConvert by lazy {
        converts.firstOrNull {
            it.getOriginType() == keyType
        }

    }
    val valueConvert by lazy {
        converts.firstOrNull {
            it.getOriginType() == valueType
        }
    }

    val keyCodeWithConvert by lazy {
        keyConvert?.let { ObjectCodeWiterWithConverter(keyType, it) } ?: ObjectCodeWiter(keyType)
    }
    val valueCodeWithConvert by lazy {
        valueConvert?.let { ObjectCodeWiterWithConverter(valueType, it) }
                ?: ObjectCodeWiter(valueType)
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {
        builder.apply {
            addStatement("int[] ${tempName()}_locations= \$N.pickOffsetArray(${id * 2 + 4})$NEW_LINE", unpackHelper)
            beginControlFlow("if(${tempName()}_locations!=null)")
            addStatement("\$T map = new \$T()", HashMap::class.java, HashMap::class.java)
            addStatement("${getValue()}=map")
            addStatement("\$T ${tempName()}_Pair =new \$T()", Pair::class.java, Pair::class.java)

            val keyLoc = "${keyItemName}_loc"
            val valueLoc = "${valueItemName}_loc"
            addStatement("int $keyLoc=0")
            addStatement("int $valueLoc=0")

            initKeyItem(builder)
            initValueItem(builder)

            beginControlFlow("for(int offset:${tempName()}_locations)")
            addStatement("${tempName()}_Pair.__assign(offset,\$N.getBb())", unpackHelper)
            addStatement("$keyLoc=${tempName()}_Pair.key()")
            addStatement("$valueLoc=${tempName()}_Pair.value()")
            beginControlFlow("if(!\$N.invalid($keyLoc))", unpackHelper)
            keyCodeWithConvert.unpackObj(builder, unpackHelper, keyLoc, keyItemName)
            endControlFlow()
            beginControlFlow("if(!\$N.invalid($valueLoc))", unpackHelper)
            valueCodeWithConvert.unpackObj(builder, unpackHelper, valueLoc, valueItemName)
            endControlFlow()
            addStatement("map.put($keyItemName,$valueItemName)")
            addStatement("$keyItemName=null")
            addStatement("$valueItemName=null")
            endControlFlow()
            endControlFlow()
        }
    }

    override fun needGenerateOffset(): Boolean {
        return true
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder) {
        val parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Map.Entry::class.java), keyType, valueType)
        builder.apply {
            beginControlFlow("if(${getValue()}!=null)")
            addStatement("int size=${getValue()}.size()")
            addStatement("int index=0")
            addStatement("int[] offsetArray=new int[size]")
            val keyOffset = "keyOffset"
            val valueOffset = "valueOffset"

            initKeyItem(builder)
            initValueItem(builder)

            addStatement("int $keyOffset=0");
            addStatement("int $valueOffset=0")
            beginControlFlow("for(\$T entry : ${getValue()}.entrySet())", parameterizedTypeName)
            addStatement(" $keyItemName=entry.getKey()")
            addStatement(" $valueItemName=entry.getValue()")
            keyCodeWithConvert.packObject(builder, packHelper, keyItemName, keyOffset)
            valueCodeWithConvert.packObject(builder, packHelper, valueItemName, valueOffset)

            addStatement("offsetArray[index++]=\$T.createPair(\$N,$keyOffset,$valueOffset)", Pair::class.java, packHelper)
            addStatement("$keyOffset=0")
            addStatement("$valueOffset=0")
            endControlFlow()
            addStatement("$offsetName=\$N.createVectorOfTables(offsetArray)$NEW_LINE", packHelper)
            endControlFlow()
        }


    }

    val keyItemName: String
        get() {
            return "${tempName()}_key"
        }
    val valueItemName: String
        get() {
            return "${tempName()}_value"
        }

    fun initKeyItem(builder: MethodSpec.Builder) {

        builder.addStatement("\$T $keyItemName=null", keyType)
    }

    fun initValueItem(builder: MethodSpec.Builder) {
        builder.addStatement("\$T $valueItemName=null", valueType)
    }

}