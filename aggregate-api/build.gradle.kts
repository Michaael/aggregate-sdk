val jarImplementation by configurations.creating

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/java")
            exclude("**/*.java")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
    }
    // Source set для бенчмарков JMH
    create("jmh") {
        java {
            srcDir("src/jmh/java")
        }
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

dependencies {
    api(ApacheCommonsLibs.commonsNet)
    api(ApacheCommonsLibs.commonsBeanutils)
    api(ApacheCommonsLibs.commonsLogging)
    api(ApacheCommonsLibs.commonsLang3)
    api(ApacheCommonsLibs.commonsIo)
    api(ApacheCommonsLibs.commonsMath3)

    api(Log4JLibs.log4jApi)
    api(Log4JLibs.log4jCore)
    api(Log4JLibs.log4j12Api)
    api(Log4JLibs.log4jSlf4jImpl)
    api(Log4JLibs.slf4jApi)

    // JMH для бенчмарков производительности
    testImplementation(JMHLibs.jmhCore)
    testAnnotationProcessor(JMHLibs.jmhGeneratorAnnProcess)

    // Используем локальные файлы из libs для javacsv и jpf
    val libsDir = file("../libs")
    if (libsDir.exists() && libsDir.isDirectory) {
        libsDir.listFiles()?.filter { it.name.endsWith(".jar") }?.forEach { jarFile ->
            api(files(jarFile))
        }
    }
    
    api("xalan", "xalan", "2.7.2")
    api("com.googlecode.json-simple", "json-simple", "1.1.1") {
        exclude(group = "junit", module = "junit")
    }

    api("com.google.code.findbugs", "jsr305", "3.0.2")       // @Nonnull and @Nullable annotations
    api(GoogleLibs.guava)
}

tasks.named("assemble") {
    dependsOn(":widget-api:assemble")
}

tasks.withType<Jar> {
    dependsOn(":widget-api:compileJava")
    dependsOn(":widget-api:processResources")
    from(jarImplementation.asFileTree.files.map { zipTree(it) })
    from("../widget-api/build/classes/java/main")
    from("../widget-api/build/classes/java/test") {
        exclude("Test*")
    }
    from("../widget-api/build/resources/main")
    exclude("**/*.jar")
}

// Конфигурация для JMH source set
configurations {
    create("jmh")
}

dependencies {
    // JMH зависимости для source set
    add("jmh", JMHLibs.jmhCore)
    add("jmh", JMHLibs.jmhGeneratorAnnProcess)
    add("jmh", sourceSets.main.get().output)
    add("jmh", sourceSets.test.get().output)
    add("jmhCompileOnly", JMHLibs.jmhGeneratorAnnProcess)
}

// Задача для компиляции бенчмарков
tasks.register<JavaCompile>("compileJmh") {
    group = "benchmark"
    source = sourceSets["jmh"].java
    classpath = sourceSets["jmh"].compileClasspath
    destinationDirectory.set(file("$buildDir/classes/jmh"))
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

// Задача для создания JAR с бенчмарками
tasks.register<Jar>("jmhJar") {
    group = "benchmark"
    dependsOn("compileJmh", "testClasses")
    archiveClassifier.set("jmh")
    
    from(sourceSets["jmh"].java.classesDirectory) {
        include("**/*.class")
    }
    from(sourceSets.main.get().output)
    from(sourceSets.test.get().output)
    
    manifest {
        attributes("Main-Class" to "org.openjdk.jmh.Main")
    }
}

// Задача для запуска бенчмарков
tasks.register<JavaExec>("jmh") {
    group = "benchmark"
    description = "Run JMH benchmarks. Use --args to pass JMH options."
    dependsOn("jmhJar")
    
    classpath = configurations.getByName("jmh") + files(tasks.named<Jar>("jmhJar").get().archiveFile.get().asFile)
    mainClass.set("org.openjdk.jmh.Main")
    
    // Параметры по умолчанию для JMH
    // Можно переопределить через командную строку:
    // ./gradlew :aggregate-api:jmh --args="PathParsingBenchmark -rf json -rff results.json"
    if (project.hasProperty("jmhArgs")) {
        args = (project.property("jmhArgs") as String).split(" ")
    } else {
        args = listOf("-h") // Показать справку по умолчанию
    }
}