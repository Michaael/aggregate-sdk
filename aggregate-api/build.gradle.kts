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