plugins {
    id("multiplatform-all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":nl-taxes"))
                api(project(":mortgage"))
            }
        }
    }
}
