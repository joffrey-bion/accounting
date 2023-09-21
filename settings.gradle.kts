dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "accounting"

include("cli-experiments")
include("money")
include("mortgage")
include("nl-taxes")
include("ui-web")
