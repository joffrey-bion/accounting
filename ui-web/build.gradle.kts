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
        val jsMain by getting {
            dependencies {
                implementation(platform(libs.kotlin.wrappers.bom.get()))
                implementation(libs.kotlin.wrappers.react.core)
                implementation(libs.kotlin.wrappers.react.dom)
            }
        }
    }
}

tasks.named<ProcessResources>("jsProcessResources") {
    val webpack = project.tasks.withType(KotlinWebpack::class).first()

    val bundleFile = webpack.mainOutputFileName
    val publicPath = "./" // TODO get public path from webpack config

    filesMatching("*.html") {
        expand("bundle" to bundleFile.get(), "publicPath" to publicPath)
    }
}
