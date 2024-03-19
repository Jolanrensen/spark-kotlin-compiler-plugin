package org.jetbrains.kotlinx.spark.plugin

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.kotlinx.spark.plugin.runners.AbstractBoxTest
import org.jetbrains.kotlinx.spark.plugin.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "testData", testsRoot = "test-gen") {
//            testClass<AbstractDiagnosticTest> {
//                model("diagnostics")
//            }

            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
}
