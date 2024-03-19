
plugins {
    java
    kotlin("jvm") version "2.0.0-Beta4"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
}
group = "org.jetbrains.kotlinx.spark.plugin"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

