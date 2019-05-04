package com.loong.chow.smartpacket.compiler.field

import com.loong.chow.smartpacket.compiler.converter.TypeConvertInterface
import com.squareup.javapoet.*

open class ListCodeDefWithConvert(override val id: Int, override val fieldOwner: String?, override val fieldName: String, override val fieldType: TypeName, override val offsetName: String, override val unpackHelper: ParameterSpec,
                                  override val packHelper: ParameterSpec, override val converts: MutableSet<TypeConvertInterface>) : SnapCodeDefWithConvert(id, fieldOwner, fieldName, fieldType, offsetName, unpackHelper, packHelper, converts) {


    open val comType by lazy {
        val comp = (fieldType as ParameterizedTypeName).typeArguments[0]
        comp
    }


    val compCodeWithConvert by lazy {
        converts.firstOrNull {
            it.getOriginType().withoutAnnotations() == comType.withoutAnnotations()
        }?.let { ObjectCodeWiterWithConverter(comType, it) }
                ?: ObjectCodeWiter(comType).apply { this.logger = this@ListCodeDefWithConvert.logger }
    }

    fun initItemName(builder: MethodSpec.Builder) {
        builder.addStatement("\$T ${getItemName()}=null", comType)
    }

    fun getItemName(): String {
        return "${tempName()}_item"
    }

    override fun generateUnpackCode(builder: MethodSpec.Builder) {


        assert(comType is ClassName)


        builder.apply {
            addStatement("int[] ${getItemName()}_locations=\$N.pickOffsetArray(${id * 2 + 4})", unpackHelper)
            beginControlFlow("if( ${getItemName()}_locations!=null)")

            initResult(builder, "${getItemName()}_locations.length")
            initItemName(builder)
            addStatement("int index_${getItemName()}=0")
            beginControlFlow("for(int location: ${getItemName()}_locations)")
            beginControlFlow("if(!\$N.invalid(location))", unpackHelper)
            compCodeWithConvert.unpackObj(builder, unpackHelper, "location", getItemName())
            endControlFlow();
            setIndexValue(builder, "index_${getItemName()}", getItemName())
            addStatement("${getItemName()}=null")
            addStatement("index_${getItemName()}++")
            endControlFlow()
            endControlFlow()
        }


    }


    open fun initResult(builder: MethodSpec.Builder, size: String) {
        val type = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), comType);
        builder.addStatement("\$T ${fieldName}_tmp=new \$T($size)", type, type)
        setValue(builder, "${fieldName}_tmp")
    }

    override fun generateOffsetCode(builder: MethodSpec.Builder) {

        builder.apply {
            beginControlFlow("if(${getValue()}!=null)")
            addStatement("int[] ${getItemName()}_locations=new int[${getLength()}]")

            initItemName(builder)
            beginControlFlow("for(int i = ${getItemName()}_locations.length-1;i>=0;i--)")

            addStatement("${getItemName()}=${getValue("i")}")
            compCodeWithConvert.packObject(builder, packHelper, getItemName(), "${getItemName()}_locations[i]")
            endControlFlow();
            addStatement("$offsetName= \$N.createVectorOfTables(${getItemName()}_locations)", packHelper)
            endControlFlow()
        }
    }

    open fun setIndexValue(builder: MethodSpec.Builder, index: String, item: String) {


        builder.addStatement("${getValue()}.add($item)")

    }

    open fun getValue(i: String): String {

        return "${getValue()}.get(i)"
    }

    open fun getLength(): String {
        return "${getValue()}.size()"
    }

    override fun packFieldDirect(builder: MethodSpec.Builder) {
        // do nothing
    }

    override fun needGenerateOffset(): Boolean {
        return true;
    }

}