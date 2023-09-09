plugins {
    id("multiplatform-all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":money"))
            }
        }
    }
}