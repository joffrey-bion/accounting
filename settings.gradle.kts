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

rootProject.name = "accounting"

include("cli-experiments")
include("money")
include("mortgage")
include("nl-taxes")
include("test-data")
include("ui-compose")
include("ui-web-react")
