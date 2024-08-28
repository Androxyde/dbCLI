plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.library") version "4.4.2"
    id("io.micronaut.aot") version "4.4.2"
}

version = "0.1"
group = "org.androxyde"

repositories {
    mavenCentral()
}

dependencies {

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")

    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.validation:micronaut-validation")

    implementation("info.picocli:picocli:4.7.6")
    implementation("org.buildobjects:jproc:2.8.2")
    implementation("ch.qos.logback:logback-core:1.5.7")
    implementation("ch.qos.logback:logback-classic:1.5.7")
    implementation("org.apache.commons:commons-exec:1.4.0")
    implementation("org.apache.sshd:sshd-cli:2.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.17.2")

    compileOnly("org.projectlombok:lombok")

}

micronaut {
    processing {
        incremental(true)
        annotations("org.androxyde.*")
    }
}
