dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("com.gradle.develocity") version "4.3.2"
}

rootProject.name = "accounting"

include("cli-experiments")
include("money")
include("mortgage")
include("nl-taxes")
include("test-data")
include("ui-compose")

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground = false // background upload is bad for CI, and not critical for local runs
    }
}
