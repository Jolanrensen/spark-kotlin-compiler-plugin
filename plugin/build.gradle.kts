import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
}

val kotlinVersion: String by project.properties

//buildConfig {
//    packageName(project.group.toString())
//    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
//    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
//    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
//    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
//}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

sourceSets {
    test {
        java.setSrcDirs(listOf("src/test-gen/kotlin"))
    }
}


dependencies {
    "org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion".let {
        compileOnly(it)
        testImplementation(it)
    }

    testRuntimeOnly("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-annotations-jvm:$kotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-internal-test-framework:$kotlinVersion")
    testImplementation("junit:junit:4.13.2")

    testImplementation(platform("org.junit:junit-bom:5.8.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-commons")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-runner")
    testImplementation("org.junit.platform:junit-platform-suite-api")
}


tasks.test {
    dependsOn(project(":plugin-annotations").tasks.getByName("jar"))
    useJUnitPlatform()
    doFirst {
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        languageVersion = "2.0"
        freeCompilerArgs = freeCompilerArgs +
                "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi" +
                "-Xcontext-receivers"
    }
}

val generateTests by tasks.creating(JavaExec::class) {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("org.jetbrains.kotlinx.spark.plugin.GenerateTestsKt")
}

val compileTestKotlin by tasks.getting {
    doLast {
        generateTests.exec()
    }
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations
        .testRuntimeClasspath.get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}