plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.3.6"
    id("io.micronaut.aot") version "4.3.6"
}

version = "0.1"
group = "org.androxyde"

repositories {
    mavenCentral()
}

sourceSets {
    this.getByName("main"){
        this.java.srcDir("../src/main/java")
        this.resources.srcDir("../src/main/resources")
    }
    this.getByName("test"){
        this.java.srcDir("../src/test/java")
        this.resources.srcDir("../src/test/resources")
    }
}

dependencies {

    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("info.picocli:picocli-codegen:4.7.5")

    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("org.projectlombok:lombok:1.18.32")

    implementation("io.micronaut.picocli:micronaut-picocli:4.7.5")
    implementation("io.micronaut:micronaut-aop")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-websocket")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.session:micronaut-session")
    implementation("io.micronaut:micronaut-http-server")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")

    implementation("io.micronaut.security:micronaut-security")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("info.picocli:picocli")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")

    implementation("com.fasterxml.uuid:java-uuid-generator:5.0.0")
    implementation("ch.qos.logback:logback-core:1.5.4")
    implementation("org.buildobjects:jproc:2.8.2")
    implementation("org.apache.commons:commons-lang3:3.14.0")


    implementation("ch.qos.logback:logback-classic:1.5.4")
    runtimeOnly("org.slf4j:slf4j-ext:2.0.13")
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("com.h2database:h2")

}

application {
    mainClass.set("org.androxyde.Main")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

tasks.withType<Jar> {

    // Exclude some ressources from jar content
    exclude("application*yml")

    manifest {
        attributes["Main-Class"] = "org.androxyde.Main"
        attributes["Premain-Class"] = "org.androxyde.Agent"
        attributes["Launcher-Agent-Class"] = "org.androxyde.Agent"
    }

}

micronaut {
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("org.androxyde.*")
    }
}