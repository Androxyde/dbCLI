plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.3.5"
    id("io.micronaut.aot") version "4.3.5"
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

    annotationProcessor("info.picocli:picocli-codegen:4.7.5")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("org.projectlombok:lombok:1.18.32")

    implementation("io.micronaut.picocli:micronaut-picocli:4.7.5")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")

    implementation("info.picocli:picocli")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")

    implementation("com.fasterxml.uuid:java-uuid-generator:5.0.0")
    implementation("ch.qos.logback:logback-core:1.5.3")

    implementation("ch.qos.logback:logback-classic:1.5.3")
    runtimeOnly("org.slf4j:slf4j-ext:2.0.12")
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