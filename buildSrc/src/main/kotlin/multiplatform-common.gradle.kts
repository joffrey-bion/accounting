import gradle.kotlin.dsl.accessors._26117b6009988811d1a974d5ceeedb52.kotlin
import org.jetbrains.kotlin.gradle.*

plugins {
    kotlin("multiplatform")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}
