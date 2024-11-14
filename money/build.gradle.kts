plugins {
    id("multiplatform-all")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.bignum) // exposed for RoundingMode
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
