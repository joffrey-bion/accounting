plugins {
    id("multiplatform-all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":money"))
            }
        }
    }
}