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
        commonTest {
            dependencies {
                api(kotlin("test"))
            }
        }
    }
}
