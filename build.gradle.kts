import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://jitpack.io") {
        name = "JitPack"
    }
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:pylon-core:0.20.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib*"))
        exclude(dependency("org.jetbrains:annotations*"))
    }
}

tasks.runServer {
    downloadPlugins {
        github("pylonmc", "pylon-core", "0.20.0", "pylon-core-0.20.0.jar")
        github("pylonmc", "pylon-base", "0.13.1", "pylon-base-0.13.1.jar")
    }
    minecraftVersion("1.21.10")
}

paper {
    name = "Galactipylon"
    version = project.version.toString()
    main = "io.github.seggan.galactipylon.Galactipylon"
    bootstrapper = "io.github.seggan.galactipylon.Bootstrapper"
    apiVersion = "1.21.10"
    authors = listOf("Seggan")
    description = "A Pylon module adding space-themed content."
    bootstrapDependencies {
        register("PylonCore") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
    serverDependencies {
        register("PylonCore") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}
