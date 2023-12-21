buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}