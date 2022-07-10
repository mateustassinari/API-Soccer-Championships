import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("war")
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    kotlin("plugin.jpa") version "1.3.61"
    id("maven-publish")
    id("org.sonarqube") version "2.8"
}

group = "com.mateus"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

springBoot {
    mainClassName = "br.com.mateus.projetoRestPuc.ProjetoRestApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //providedRuntime ("org.springframework.boot:spring-boot-starter-tomcat")
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
    implementation ("org.bytedeco:javacv-platform:1.3.1")
    implementation("org.projectlombok:lombok:1.18.22")
    runtimeOnly("mysql:mysql-connector-java:8.0.27")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
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
