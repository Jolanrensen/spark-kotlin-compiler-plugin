import com.github.gmazzo.buildconfig.BuildConfigExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    java
    kotlin("jvm") version "2.0.0-Beta4" apply false
    id("com.github.gmazzo.buildconfig") version "5.3.5" apply false
    id("com.vanniktech.maven.publish") version "0.25.2" apply false
}
group = "org.jetbrains.kotlinx.spark.plugin"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }

    afterEvaluate {

        extensions.findByType<BuildConfigExtension>()?.apply {

            val currentVersion = project.version.toString()
            val compilerPluginId = "org.jetbrains.kotlinx.spark.plugin"

            val groupId = "org.jetbrains.kotlinx.spark.plugin"
            val compilerPluginArtifactId = "plugin"
            val gradlePluginId = "gradle-plugin"
            val pluginAnnotationsId = "plugin-annotations"

            packageName(groupId)
            className("Artifacts")

            buildConfigField("String", "compilerPluginId", "\"$compilerPluginId\"")
            buildConfigField("String", "groupId", "\"$groupId\"")
            buildConfigField("String", "gradlePluginId", "\"$gradlePluginId\"")
            buildConfigField("String", "currentVersion", "\"$currentVersion\"")
            buildConfigField("String", "pluginAnnotationsId", "\"$pluginAnnotationsId\"")
            buildConfigField("String", "compilerPluginArtifactId", "\"$compilerPluginArtifactId\"")
        }
        extensions.findByType<KotlinJvmProjectExtension>()?.apply {
            jvmToolchain(8)
        }
        extensions.findByType<JavaPluginExtension>()?.apply {
            toolchain {
                languageVersion = JavaLanguageVersion.of(8)
            }
        }
    }
}
