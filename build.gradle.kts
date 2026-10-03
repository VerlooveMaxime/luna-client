/*
 * Gradle build for the Luna #377 client (added by the IdleRS fork; upstream ships plain sources only).
 *
 * Sources live directly in ./src (default package + `sign` + `luna` packages), no external dependencies.
 * The client expects to run with this directory as working dir: ./cache/ (game cache) and ./rsa/rsapub.toml.
 * Tests of our own code live in ./test rather than src/test/java, since everything under ./src is compiled as the client.
 */
plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(emptyList<String>())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-nowarn")
}

application {
    mainClass = "client"
}

tasks.test {
    useJUnitPlatform()
}

// Line and branch coverage of our own code, the idlers packages, with the server's ratchet tasks and summary. The
// default-package glue (IdleWidgets, IdleWorldMap) only wires the obfuscated client to them and is not counted.
extra["idlersCoverageName"] = "client"
extra["idlersCoverageClasses"] = sourceSets["main"].output.classesDirs.asFileTree.matching { include("idlers/**/*.class") }
extra["maxMissedLines"] = 0
extra["maxMissedBranches"] = 0
extra["idlersRatchetFile"] = "luna-client/build.gradle.kts"
apply(from = rootDir.resolve("luna/idlers-coverage.gradle.kts"))

tasks.named<JavaExec>("run") {
    workingDir = projectDir
}
