package org.jetbrains.kotlinx.spark.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlinx.spark.plugin.ir.SparkifyIrGenerationExtension

class SimplePluginComponentRegistrar: CompilerPluginRegistrar() {
    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val sparkifyAnnotationFqNames = listOf("org.jetbrains.kotlinx.spark.plugin.Sparkify")
        IrGenerationExtension.registerExtension(
            SparkifyIrGenerationExtension(sparkifyAnnotationFqNames)
        )
    }
}
