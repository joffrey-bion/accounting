import org.jetbrains.compose.desktop.application.dsl.*
import org.jetbrains.kotlin.gradle.*
import org.jetbrains.kotlin.gradle.targets.js.webpack.*

plugins {
    id("multiplatform-common")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.composeHotReload)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "ui-compose"
        browser {
            commonWebpackConfig {
                outputFileName = "ui-compose.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                        add(project.projectDir.path + "/commonMain/")
                        add(project.projectDir.path + "/wasmJsMain/")
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mortgage"))
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.koalaplot)
            }
        }
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.hildan.accounting.ui.desktop.DesktopMainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.hildan.accounting"
            packageVersion = project.version.toString().takeIf { it != "unspecified" } ?: "1.0.0"
        }
    }
}
