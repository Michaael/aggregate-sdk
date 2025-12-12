dependencies {
    api(project(":aggregate-api"))
    testImplementation(project(":aggregate-api").dependencyProject.sourceSets.test.get().output)
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(File("."))
    from("build/classes/java/main")
    from("build/classes/java/test")
    from("build/resources/main")
}