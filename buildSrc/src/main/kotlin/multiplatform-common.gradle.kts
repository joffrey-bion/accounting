plugins {
    kotlin("multiplatform")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}
