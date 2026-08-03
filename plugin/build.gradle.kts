import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
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
    maven("https://repo.codemc.io/repository/maven-releases/") {
        name = "CodeMC"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("io.github.pylonmc:rebar:$rebarVersion")
}

kotlin {
    jvmToolchain(25)
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
    apiVersion = "26.1"
    authors = listOf("Seggan")
    description = "The Pylon continuation of Galactifun2"
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
