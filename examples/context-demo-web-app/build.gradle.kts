val jarName = project.name.substringAfter("-")

plugins {
    war
}

dependencies {
    implementation(project(":aggregate-api"))
    testImplementation(project(":aggregate-api").dependencyProject.sourceSets.test.get().output)
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
    }
}

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("demo-web-app.jar")
        destinationDirectory.set(File("."))
        from("build/classes/java/main")
        from("plugin.xml")
    }
    clean {
        delete("$jarName.jar")
    }
    withType<War> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("demo-web-app.war")
        destinationDirectory.set(File("."))
        webXml = File("web.xml")
        from("build/classes/java/main")
        clean {
            delete("$jarName.war")
        }
    }
}