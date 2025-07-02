import org.jetbrains.kotlin.gradle.*

plugins {
    id("multiplatform-common")
}

kotlin {
    jvm()
    js {
        browser()
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
}

val generateKarmaConfig by project.tasks.registering {
    group = "js test setup"
    description = "Generates a Karma configuration that increases the Mocha timeout for browser tests."

    val karmaConfigFile = layout.projectDirectory.file("karma.config.d/mocha-timeout-config.js")
    outputs.file(karmaConfigFile)

    doFirst {
        // language=javascript
        karmaConfigFile.asFile.writeText("""            
            // To increase the internal mocha test timeout (cannot be done from DSL)
            // https://youtrack.jetbrains.com/issue/KT-56718#focus=Comments-27-6905607.0-0
            config.set({
                client: {
                    mocha: {
                        // We put a large timeout here so we can adjust it in the tests themselves.
                        timeout: 60000
                    }
                }
            });
        """.trimIndent())
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest> {
    dependsOn(generateKarmaConfig)
}
