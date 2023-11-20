plugins {
    id("groovy") 
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.0"
}

version = "0.1"
group = "org.androxyde"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("info.picocli:picocli")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    runtimeOnly("ch.qos.logback:logback-classic")
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