import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}
group = "com.luismedinaweb"
version = ""

repositories {
    jcenter()
    mavenCentral()
    maven {
        url = uri("C:/Users/luiso/.m2/repository")
    }
}
dependencies {
    implementation("com.luismedinaweb:LinkerProtocol:1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.google.code.gson:gson:2.8.6")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.luismedinaweb.LinkerServerKt"
        )
    }
    from(sourceSets.main.get().output)
    from(sourceSets.main.get().resources)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}