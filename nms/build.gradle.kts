plugins {
    kotlin("jvm")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "io.github.seggan"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":plugin"))
    paperweight.paperDevBundle("26.2.build.+")
}

kotlin {
    jvmToolchain(25)
}