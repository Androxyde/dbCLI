plugins {
    id("groovy") 
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "3.7.10"
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

    annotationProcessor("info.picocli:picocli-codegen:4.7.5")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")

    implementation("info.picocli:picocli:4.7.5")
    implementation("io.micronaut.picocli:micronaut-picocli")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("net.bytebuddy:byte-buddy:1.14.13")

    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
    runtimeOnly("org.slf4j:slf4j-api:2.0.12")
    runtimeOnly("org.yaml:snakeyaml:2.2")

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
    testRuntime("JUnit")
    processing {
        incremental(true)
        annotations("org.androxyde.*")
    }
}