pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
    
}
rootProject.name = "spark-kotlin-compiler-plugin"

include("plugin")
include("plugin-annotations")
