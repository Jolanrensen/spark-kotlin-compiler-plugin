package org.jetbrains.kotlinx.spark.plugin.services

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlinx.spark.plugin.ir.SparkifyIrGenerationExtension

class ExtensionRegistrarConfigurator(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration,
    ) {
        val sparkifyAnnotationFqNames = listOf("org.jetbrains.kotlinx.spark.plugin.Sparkify")
        IrGenerationExtension.registerExtension(
            SparkifyIrGenerationExtension(sparkifyAnnotationFqNames)
        )
    }
}