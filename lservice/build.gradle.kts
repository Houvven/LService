plugins {
    `maven-publish`
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
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    api(libs.libsu.service)
    api(libs.rikkax.parcelablelist)
    compileOnly(fileTree("libs") { include("*.jar") })
}

publishing {
    publications {
        register<MavenPublication>("LService") {
            group = "io.github.houvven"
            artifactId = "lservice"
            version = "1.0.0"
            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }

    repositories {
        mavenLocal()
    }
}