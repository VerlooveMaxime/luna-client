/*
 * Gradle build for the Luna #377 client (added by the IdleRS fork; upstream ships plain sources only).
 *
 * Sources live directly in ./src (default package + `sign` + `luna` packages), no external dependencies.
 * The client expects to run with this directory as working dir: ./cache/ (game cache) and ./rsa/rsapub.toml.
 */
plugins {
    java
    application
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
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-nowarn")
}

application {
    mainClass = "client"
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir
}
