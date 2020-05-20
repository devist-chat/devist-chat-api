import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("java")
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
}

group = "io.devist"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-validator:commons-validator:1.6")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.flywaydb.flyway-test-extensions:flyway-spring-test:5.0.0")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.register("projectVersion") {
    doLast {
        print("project-version: ")
        println(project.version)
    }
}

tasks.register("projectGroup") {
    doLast {
        print("project-group: ")
        println(project.group)
    }
}

tasks.register("projectName") {
    doLast {
        print("project-name: ")
        println(project.name)
    }
}


tasks.register("setBuildNumber") {

    doLast {
        println(this.hasProperty("buildNumber"))
        println(this.project.properties)
        println("Changing build number to ${buildNumber}...")

        val currentVersion = project.version as String

        val versionNumber = currentVersion.split("-")[0]
        val release = currentVersion.split("-")[1]
        val versionNumbers = versionNumber.split(".")

        var version = StringBuilder("")

        for (i in 0..(versionNumbers.size - 2)) {
            version.append(versionNumbers[i])
            version.append(".")
        }

        version.append(buildNumber)
        version.append("-")
        version.append(release)

        project.version = version.toString()
        print("project-version: ")
        println(project.version)
    }
}