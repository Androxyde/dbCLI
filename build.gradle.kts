plugins {
    id("groovy") 
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
        this.java.srcDir("C:\\Users\\eodille\\git\\dbCLI_sources\\src\\main\\java")
        this.resources.srcDir("C:\\Users\\eodille\\git\\dbCLI_sources\\src\\main\\resources")
    }
    this.getByName("test"){
        this.java.srcDir("C:\\Users\\eodille\\git\\dbCLI_sources\\src\\test\\java")
        this.groovy.srcDir("C:\\Users\\eodille\\git\\dbCLI_sources\\src\\test\\groovy")
        this.resources.srcDir("C:\\Users\\eodille\\git\\dbCLI_sources\\src\\test\\resources")
    }
}

dependencies {

    annotationProcessor("info.picocli:picocli-codegen")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("org.projectlombok:lombok:1.18.30")

    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")

    implementation("info.picocli:picocli")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

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
    // Otherwise you'll get a "No main manifest attribute" error
    manifest {
        attributes["Main-Class"] = "org.androxyde.Main"
        attributes["Premain-Class"] = "org.androxyde.Agent"
        attributes["Launcher-Agent-Class"] = "org.androxyde.Agent"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

micronaut {
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("org.androxyde.*")
    }
}