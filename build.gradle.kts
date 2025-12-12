plugins {
    java
    id("org.jetbrains.gradle.plugin.idea-ext") version "0.7"
    id("com.diffplug.spotless") version "6.11.0" apply false
    // JaCoCo is a core Gradle plugin, version is determined by Gradle version
    // For Java 8 compatibility, we use the version that comes with Gradle 8.11
}

tasks {
    named<Wrapper>("wrapper") {
        gradleVersion = "8.11"
        distributionType = Wrapper.DistributionType.ALL
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    repositories {
        mavenCentral()
        maven(url = "https://repo1.maven.org/maven2/")
        google()
        maven(url = "https://jitpack.io")
        // Временно отключен из-за проблем с SSL
        // maven(url = "https://store.aggregate.digital/repository/maven-public")
    }
    configurations.all {
        resolutionStrategy {
            // Удалено принудительное использование устаревшей версии commons-codec
            // Теперь используется актуальная версия 1.16.0
        }
        exclude(group = "xerces", module = "xercesImpl")
        exclude(group = "xerces", module = "xmlParserAPIs")
    }
    tasks.withType<JavaCompile> {
        options.isFork = true
        options.isIncremental = true
        options.isWarnings = false
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    if (name.startsWith("demo-") || name == "aggregate-api") {
        dependencies {
            testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
            testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
            testImplementation("org.junit.platform:junit-platform-suite:6.0.1")
            testImplementation("org.awaitility", "awaitility", "4.2.1")
            testImplementation(XStreamLibs.hamcrestAll)
            testImplementation(MockitoLibs.mockitoCore)
            testImplementation("org.mockito:mockito-junit-jupiter:${MockitoLibs.Versions.mockitoCoreVersion}")
        }
    }
}

project(":context-demo-web-app") {
    tasks.withType<JavaCompile> {
        onlyIf {
            project.hasProperty("web")
        }
    }
}

// Apply Spotless code formatting and JaCoCo to all Java projects
subprojects {
    if (name != "buildSrc") {
        apply(plugin = "com.diffplug.spotless")
        
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            java {
                target("src/**/*.java")
                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
                // Используем версию, совместимую с Java 8
                // Google Java Format 1.10.x - последняя версия, поддерживающая Java 8
                googleJavaFormat("1.10.0").aosp().reflowLongStrings()
            }
            
            kotlin {
                target("**/*.kt")
                ktlint("0.50.0")
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
        
        // Apply JaCoCo for test coverage (Gradle 8.11 includes JaCoCo that supports Java 8)
        if (name.startsWith("demo-") || name == "aggregate-api") {
            apply(plugin = "jacoco")
            
            afterEvaluate {
                tasks.named("jacocoTestReport", org.gradle.testing.jacoco.tasks.JacocoReport::class.java) {
                    dependsOn(tasks.test)
                    reports {
                        xml.required.set(true)
                        html.required.set(true)
                        csv.required.set(false)
                    }
                }
                
                tasks.named("jacocoTestCoverageVerification", org.gradle.testing.jacoco.tasks.JacocoCoverageVerification::class.java) {
                    dependsOn(tasks.named("jacocoTestReport"))
                    violationRules {
                        rule {
                            limit {
                                minimum = "0.30".toBigDecimal() // Минимум 30% покрытия
                            }
                        }
                    }
                }
                
                tasks.named("check") {
                    dependsOn(tasks.named("jacocoTestCoverageVerification"))
                }
            }
        }
    }
}

// Aggregate coverage report for all subprojects
tasks.register<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.withType<Test>() })
    
    val jacocoReportTasks = subprojects
        .filter { it.name.startsWith("demo-") || it.name == "aggregate-api" }
        .map { it.tasks.named<org.gradle.testing.jacoco.tasks.JacocoReport>("jacocoTestReport") }
    
    executionData.setFrom(jacocoReportTasks.map { it.get().executionData })
    
    sourceDirectories.setFrom(
        files(jacocoReportTasks.map { it.get().sourceDirectories })
    )
    
    classDirectories.setFrom(
        files(jacocoReportTasks.map { it.get().classDirectories })
    )
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}



