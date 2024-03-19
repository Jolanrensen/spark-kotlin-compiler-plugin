package org.jetbrains.kotlinx.spark.plugin

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SimplePluginRegistrar(private val sparkifyAnnotationFqNames: List<String>) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
//        +::SimpleClassGenerator
//        +::DataClassSuperTypesGenerator
//        +::DataClassFunctionGenerator
//        +FirSparkifyPredicateMatcher.getFactory(sparkifyAnnotationFqNames)
    }
}
