plugins {
    kotlin("jvm")
    application
}

application {
    mainClass = "org.hildan.accounting.cli.MainKt"
}

dependencies {
    implementation(project(":mortgage"))
    implementation(project(":test-data"))
}
