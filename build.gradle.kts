import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.14"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    war
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.3.61"
    id("maven-publish")
}

group = "com.mateus"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_13

repositories {
    mavenCentral()
}

project.configurations.compileOnly.get().isCanBeResolved = true
project.configurations.testCompileOnly.get().isCanBeResolved = true

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation("org.hibernate:hibernate-validator:5.4.3.Final")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation ("io.jsonwebtoken:jjwt:0.6.0")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("io.springfox:springfox-swagger2:2.9.2")
    implementation ("io.springfox:springfox-swagger-ui:2.9.2")
    implementation ("io.springfox:springfox-bean-validators:2.9.2")
    implementation ("com.wavefront:wavefront-spring-boot-starter:2.1.1")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    runtimeOnly("mysql:mysql-connector-java:8.0.27")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation ("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "13"
    }
}
