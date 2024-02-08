import org.jetbrains.kotlin.gradle.targets.js.webpack.*

plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        jsMain {
            dependencies {
                implementation(project(":mortgage"))
                implementation(platform(libs.kotlin.wrappers.bom.get()))
                implementation(libs.kotlin.wrappers.emotion)
                implementation(libs.kotlin.wrappers.mui.base)
                implementation(libs.kotlin.wrappers.mui.icons.material)
                implementation(libs.kotlin.wrappers.react.core)
                implementation(libs.kotlin.wrappers.react.dom)
            }
        }
    }
}

tasks.named<ProcessResources>("jsProcessResources") {
    val bundleFile = provider { tasks.withType(KotlinWebpack::class).first().mainOutputFileName.get() }
    val publicPath = "./" // TODO get public path from webpack config

    filesMatching("*.html") {
        expand("bundle" to bundleFile.get(), "publicPath" to publicPath)
    }
}
