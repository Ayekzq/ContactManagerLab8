import java.util.Properties
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}
val keystorePropertiesFile =
    rootProject.file("keystore.properties")

val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use {
            load(it)
        }
    }
}

val hasReleaseSigning = listOf(
    "storeFile",
    "storePassword",
    "keyAlias",
    "keyPassword"
).all {
    !keystoreProperties.getProperty(it).isNullOrBlank()
}

android {
    namespace = "com.example.contactmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.contactmanager"
        minSdk = 23
        targetSdk = 35

        versionCode = 3
        versionName = "1.0.1"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(
                    keystoreProperties.getProperty("storeFile")
                )

                storePassword =
                    keystoreProperties.getProperty("storePassword")

                keyAlias =
                    keystoreProperties.getProperty("keyAlias")

                keyPassword =
                    keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://dev-api.example.com/\""
            )

            resValue("string", "app_name", "Контакты · DEBUG")
        }

        create("staging") {
            initWith(getByName("debug"))

            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://staging-api.example.com/\""
            )

            resValue("string", "app_name", "Контакты · STAGING")
            matchingFallbacks += listOf("debug")
        }

        getByName("release") {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://api.example.com/\""
            )

            resValue("string", "app_name", "Контакты")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}