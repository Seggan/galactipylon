import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    kotlin("jvm")
    id("com.gradleup.shadow") version "9.0.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    //id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
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
    maven("https://repo.wyck.dev/snapshots/")
}

dependencies {
    //paperweight.paperDevBundle("26.1.2.build.+")
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("io.github.pylonmc:rebar:$rebarVersion")
    paperLibrary("dev.wyck:Wyck:3.3.0-1a0feb5")
}

kotlin {
    jvmToolchain(25)
    compilerOptions.freeCompilerArgs = listOf("-XXLanguage:+UnnamedLocalVariables")
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
        hangar("distant-horizons-support", "0.14.0")
    }
    maxHeapSize = "6G"
    jvmArgs = listOf(
        "-XX:+UseG1GC",
        "-XX:+ParallelRefProcEnabled",
        "-XX:MaxGCPauseMillis=200",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+DisableExplicitGC",
        "-XX:+AlwaysPreTouch",
        "-XX:G1NewSizePercent=30",
        "-XX:G1MaxNewSizePercent=40",
        "-XX:G1HeapRegionSize=8M",
        "-XX:G1ReservePercent=20",
        "-XX:G1HeapWastePercent=5",
        "-XX:G1MixedGCCountTarget=4",
        "-XX:InitiatingHeapOccupancyPercent=15",
        "-XX:G1MixedGCLiveThresholdPercent=90",
        "-XX:G1RSetUpdatingPauseTimePercent=5",
        "-XX:SurvivorRatio=32",
        "-XX:+PerfDisableSharedMem",
        "-XX:MaxTenuringThreshold=1",
        "-Dusing.aikars.flags=https://mcflags.emc.gs",
        "-Daikars.new.flags=true"
    )
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
