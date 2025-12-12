val playgroundGroup = "Playground"

dependencies {
    implementation(project(":aggregate-api"))
    testImplementation(project(":aggregate-api").dependencyProject.sourceSets.test.get().output)
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(File("."))
    from("build/classes/java/main")
    from("build/classes/java/test")
    from("build/resources/main")
}

task("GetServerVersion", JavaExec::class) {
    mainClass.set("examples.api.GetServerVersion")
    group = playgroundGroup
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = parent!!.projectDir
}

task("ExecuteAction", JavaExec::class) {
    mainClass.set("examples.api.ExecuteAction")
    group = playgroundGroup
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = parent!!.projectDir
}

task("ManageDevices", JavaExec::class) {
    mainClass.set("examples.api.ManageDevices")
    group = playgroundGroup
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = parent!!.projectDir
}

task("ManageUsers", JavaExec::class) {
    mainClass.set("examples.api.ManageUsers")
    group = playgroundGroup
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = parent!!.projectDir
}
