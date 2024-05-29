plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false
}
buildscript{
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            credentials {
                username = "livianeagu25"
                password = "Ldt52003@"
            }
            url = uri("https://repositories.tomtom.com/artifactory/maven")
        }
    }
}
