plugins {
    alias(libs.plugins.androidLibrary)
}


android {
    namespace = "io.github.houvven.lservice"
    compileSdk = 34


    defaultConfig {
        minSdk = 27
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {
    api(libs.libsu.service)
    api(libs.rikkax.parcelablelist)
    compileOnly(fileTree("libs") { include("*.jar") })
}