import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint")
}

java.sourceCompatibility = JavaVersion.VERSION_11

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDirectory.set(compileKotlin.destinationDirectory.get())

java {
    modularity.inferModulePath.set(true)
}

val logger4kVersion = rootProject.ext.get("versions.logger4k")
dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    implementation(project(":task-core"))
    implementation("com.github.OpenEdgn.Logger4K:logger-core:$logger4kVersion")
    testImplementation("com.github.OpenEdgn.Logger4K:logger-console:$logger4kVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.6.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}
val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
    repositories {
        mavenLocal()
    }
}
