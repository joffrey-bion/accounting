dependencyResolutionManagement {
    repositories {
        maven(url = "https://maven.pkg.jetbrains.space/data2viz/p/maven/public")
        mavenCentral()
    }
}

rootProject.name = "accounting"

include("cli-experiments")
include("money")
include("mortgage")
include("ui-web")
include("chartskt-react")
