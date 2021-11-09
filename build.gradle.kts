group = "com.GitHub.OpenEDGN"

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.0")
    }
}

allprojects {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    for (childProject in childProjects.values) {
        delete(childProject.buildDir)
    }
}
