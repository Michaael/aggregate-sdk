dependencies {
    implementation(project(":aggregate-api"))
    testImplementation(project(":aggregate-api").dependencyProject.sourceSets.test.get().output)
}