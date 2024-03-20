package org.jetbrains.kotlinx.spark.plugin.gradlePlugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlinx.spark.plugin.Artifacts

class SparkKotlinCompilerGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("sparkKotlinCompiler", SparkKotlinCompilerExtension::class.java, target)

        target.afterEvaluate {
            it.extensions.findByType<KotlinJvmProjectExtension>()?.apply {
                compilerOptions {
                    // Make sure the parameters of data classes are visible to scala
                    javaParameters.set(true)
                }
            }
        }
        target.addRuntimeSupport()
    }

    // org.jetbrains.kotlinx.spark.plugin.annotations.Sparkify is delivered by the client
    private fun Project.addRuntimeSupport() {
        val pluginAnnotations = "${Artifacts.groupId}:${Artifacts.pluginAnnotationsId}:${Artifacts.currentVersion}"

//        plugins.withId("org.jetbrains.kotlin.jvm") {
//            dependencies.add("implementation", pluginAnnotations)
//        }
//        plugins.withId("org.jetbrains.kotlin.android") {
//            dependencies.add("implementation", pluginAnnotations)
//        }
//        plugins.withId("org.jetbrains.kotlin.multiplatform") {
//            afterEvaluate {
//                extensions.configure(KotlinMultiplatformExtension::class.java) { kotlinExtension ->
//                    kotlinExtension.sourceSets.named("commonMain") {
//                        it.dependencies {
//                            implementation(pluginAnnotations)
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val target = kotlinCompilation.target.name
        val sourceSetName = kotlinCompilation.defaultSourceSet.name

        println("SparkKotlinCompilerGradlePlugin: target=$target, sourceSetName=$sourceSetName")

        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(SparkKotlinCompilerExtension::class.java)

        val enabled = extension.enabled.get()
        val sparkifyAnnotationFqNames = extension.sparkifyAnnotationFqNames.get()
            // TODO

        val outputDir = extension.outputDir.get().dir("$target/$sourceSetName/kotlin")
        kotlinCompilation.defaultSourceSet.kotlin.srcDir(outputDir.asFile)

        return project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = enabled.toString()),
                SubpluginOption(key = "sparkifyAnnotationFqNames", value = sparkifyAnnotationFqNames.joinToString()),
//                SubpluginOption(key = "outputDir", value = outputDir.toString()),
            )
        }
    }

    override fun getCompilerPluginId() = Artifacts.compilerPluginId

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = Artifacts.groupId,
            artifactId = Artifacts.compilerPluginArtifactId,
            version = Artifacts.currentVersion,
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}


