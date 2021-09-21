group = "com.GitHub.OpenEDGN"

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("org.jmailen.gradle:kotlinter-gradle:3.6.0")
    }
}

allprojects {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    for (childProject in childProjects.values) {
        delete(childProject.buildDir)
    }
}
