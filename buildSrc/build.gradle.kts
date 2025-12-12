import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://store.aggregate.digital/repository/maven-libs/")
    maven(url = "https://store.aggregate.digital/repository/maven-central/")
    maven(url = "https://store.aggregate.digital/repository/jetbrains/")
    maven(url = "https://store.aggregate.digital/repository/jitpack/")
    maven(url = "https://store.aggregate.digital/repository/m2/")
    maven(url = "https://store.aggregate.digital/repository/google/")
    maven(url = "https://store.aggregate.digital/repository/jboss/")
    maven(url = "https://store.aggregate.digital/repository/repo1/")
    maven(url = "https://store.aggregate.digital/repository/wso2/")
    maven(url = "https://store.aggregate.digital/repository/wso2dist/")
    maven(url = "https://store.aggregate.digital/repository/atlassian/")
    maven(url = "https://store.aggregate.digital/repository/oracle/")
    maven(url = "https://store.aggregate.digital/repository/wso2releases/")
}

dependencies {
    implementation("commons-net:commons-net:3.3")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}