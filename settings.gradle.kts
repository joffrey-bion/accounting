dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots") // for bignum snapshots
    }
}

rootProject.name = "accounting"

include("cli-experiments")
include("money")
include("mortgage")
include("nl-taxes")
include("ui-web-react")
