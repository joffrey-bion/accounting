dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots") // for bignum snapshots
//        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
//        google()
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
