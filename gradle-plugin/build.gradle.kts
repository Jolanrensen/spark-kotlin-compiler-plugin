plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("com.vanniktech.maven.publish")
}

group = "org.jetbrains.kotlinx.spark.plugin"
version = "0.1"


dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("gradle-plugin"))

    // Gradle plugin dependencies
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
}

gradlePlugin {
    plugins {
        create("sparkKotlinCompiler") {
            id = "org.jetbrains.kotlinx.spark.plugin.gradle-plugin"
            implementationClass = "org.jetbrains.kotlinx.spark.plugin.gradlePlugin.SparkKotlinCompilerGradlePlugin"
        }
    }
}
