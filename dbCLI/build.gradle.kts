plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.2"
    id("io.micronaut.aot") version "4.4.2"
}

version = "0.1"
group = "org.androxyde"

repositories {
    mavenCentral()
}

dependencies {

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.session:micronaut-session")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.validation:micronaut-validation")

    implementation("info.picocli:picocli:4.7.6")
    implementation("org.buildobjects:jproc:2.8.2")
    implementation("ch.qos.logback:logback-core:1.5.7")
    implementation("ch.qos.logback:logback-classic:1.5.7")
    implementation("org.apache.commons:commons-exec:1.4.0")
    implementation("org.apache.sshd:sshd-cli:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.2")

    implementation(project(":os"))
    implementation(project(":oracle"))

    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("org.projectlombok:lombok")

    runtimeOnly("org.slf4j:jul-to-slf4j:2.0.16")
    runtimeOnly("org.yaml:snakeyaml")

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