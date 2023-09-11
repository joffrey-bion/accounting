import org.jetbrains.kotlin.gradle.targets.js.webpack.*

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                compileOnly(platform(libs.kotlin.wrappers.bom.get()))
                compileOnly(libs.kotlin.wrappers.react.core)
                compileOnly(libs.kotlin.wrappers.react.dom)
                api(libs.data2viz.core)
                api(libs.data2viz.charts)
            }
        }
    }
}
