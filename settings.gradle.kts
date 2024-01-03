pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
rootProject.name = "DocumentScannerSample"
include(":DocumentScanner", ":app")
