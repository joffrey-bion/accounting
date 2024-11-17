plugins {
    id("multiplatform-all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":money"))
                api(libs.kotlinx.datetime)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
