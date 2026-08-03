import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

val rebarVersion = rootProject.property("rebar.version").toString()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.wyck.dev/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("io.github.pylonmc:rebar:$rebarVersion")
    paperLibrary("dev.wyck:Wyck:3.2.0")
}

kotlin {
    jvmToolchain(25)
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
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
    doFirst {
        project.projectDir.resolve("run/plugins").deleteRecursively()
    }
    downloadPlugins {
        github("pylonmc", "rebar", rebarVersion, "rebar-$rebarVersion.jar")
    }
    minecraftVersion("26.1.2")
}

paper {
    name = rootProject.name
    version = project.version.toString()
    main = "io.github.seggan.galactipylon.Galactipylon"
    bootstrapper = "io.github.seggan.galactipylon.Bootstrapper"
    loader = "io.github.seggan.galactipylon.Loader"
    apiVersion = "26.1"
    authors = listOf("Seggan")
    description = "The Pylon continuation of Galactifun2"
    generateLibrariesJson = true
    bootstrapDependencies {
        register("Rebar") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
    serverDependencies {
        register("Rebar") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}

tasks.generatePaperPluginDescription {
    useDefaultCentralProxy()
}
