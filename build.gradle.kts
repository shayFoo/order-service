import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"
description = "Functionality for purchasing books"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.springframework:spring-jdbc")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.wiremock.integrations:wiremock-spring-boot:3.10.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    inputs.files(tasks.named("processResources"))
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

// cloud native buildpack settings
tasks.named<BootBuildImage>("bootBuildImage") {
    environment = mapOf(
        "BP_JVM_VERSION" to "25",
        "BP_JVM_TIMEZONE" to "Asia/Tokyo",
        "LANG" to "ja_JP.UTF-8",
        "LANGUAGE" to "ja_JP:ja",
        "LC_ALL" to "ja_JP.UTF-8",
    )
    imageName = project.name + ":" + project.version
    docker {
        publishRegistry {
            username = project.findProperty("registryUsername")?.toString()
            password = project.findProperty("registryToken")?.toString()
            url = project.findProject("registryUrl")?.toString()
        }
    }
}