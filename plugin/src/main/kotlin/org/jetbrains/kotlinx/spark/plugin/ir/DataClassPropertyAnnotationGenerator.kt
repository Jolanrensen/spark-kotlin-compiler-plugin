package org.jetbrains.kotlinx.spark.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

class DataClassPropertyAnnotationGenerator(
    private val pluginContext: IrPluginContext,
    private val sparkifyAnnotationFqNames: List<String>
) : IrElementVisitorVoid {

    override fun visitElement(element: IrElement) {
        when (element) {
            is IrDeclaration,
            is IrFile,
            is IrModuleFragment -> element.acceptChildrenVoid(this)
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitProperty(declaration: IrProperty) {
        val origin = declaration.parent as? IrClass ?: return super.visitProperty(declaration)
        if (sparkifyAnnotationFqNames.none { origin.hasAnnotation(FqName(it)) })
            return super.visitProperty(declaration)

        // must be in primary constructor
        val constructorParams = declaration.parentAsClass.primaryConstructor?.valueParameters
            ?: return super.visitProperty(declaration)

        if (declaration.name !in constructorParams.map { it.name })
            return super.visitProperty(declaration)

        val getter = declaration.getter ?: return super.visitProperty(declaration)

        val jvmNameFqName = FqName(JvmName::class.qualifiedName!!)

        // remove previous JvmNames
        getter.annotations = getter.annotations
            .filterNot { it.isAnnotationWithEqualFqName(jvmNameFqName) }

        val jvmNameClassId = ClassId(jvmNameFqName.parent(), jvmNameFqName.shortName())
        val jvmName = pluginContext.referenceClass(jvmNameClassId)!!

        val jvmNameConstructor = jvmName
            .constructors
            .firstOrNull()!!

        val annotationCall = IrConstructorCallImpl.fromSymbolOwner(
            type = jvmName.defaultType,
            constructorSymbol = jvmNameConstructor,
        )
        annotationCall.putValueArgument(
            index = 0,
            valueArgument = IrConstImpl.string(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = pluginContext.irBuiltIns.stringType,
                value = declaration.name.identifier
            )
        )
        getter.annotations += annotationCall

        println("Added JvmName annotation to property ${origin.name}.${declaration.name}")
    }
}