plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "3.7.10"
    id("io.micronaut.aot") version "3.7.10"
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

    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")

    implementation("info.picocli:picocli:4.7.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.0")

    implementation("com.fasterxml.uuid:java-uuid-generator:5.0.0")
    implementation("ch.qos.logback:logback-core:1.3.14")

    implementation("ch.qos.logback:logback-classic:1.3.14")
    runtimeOnly("org.slf4j:slf4j-ext:2.0.12")
    runtimeOnly("org.yaml:snakeyaml")

}

application {
    mainClass.set("org.androxyde.Main")
}

java {
    sourceCompatibility = JavaVersion.toVersion("1.8")
    targetCompatibility = JavaVersion.toVersion("1.8")
}

tasks.withType<Jar> {
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "org.androxyde.Main"
        attributes["Premain-Class"] = "org.androxyde.Agent"
        attributes["Launcher-Agent-Class"] = "org.androxyde.Agent"
    }

}

micronaut {
    testRuntime("JUnit")
    processing {
        incremental(true)
        annotations("org.androxyde.*")
    }
}