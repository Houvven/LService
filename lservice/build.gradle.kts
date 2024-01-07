plugins {
    alias(libs.plugins.androidLibrary)
}


android {
    namespace = "io.github.houvven.lservice"
    compileSdk = 34


    defaultConfig {
        minSdk = 27
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    api(libs.libsu.core)
    api(libs.libsu.service)
    api(libs.rikkax.parcelablelist)
    compileOnly(fileTree("libs") { include("*.jar") })
}