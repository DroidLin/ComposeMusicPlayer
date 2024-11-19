plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.dev.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.music.android.lin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.music.android.lin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        aidl = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // developers

    // room
    implementation(libs.jetpack.room)
    implementation(libs.jetpack.room.ktx)
    implementation(libs.jetpack.room.paging)
    ksp(libs.jetpack.room.compiler)

    // koin
    implementation(libs.koin.core)
    implementation(libs.koin.coroutines)
    implementation(libs.koin.compose)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // exoplayer3
    implementation(libs.exoplayer3)
    implementation(libs.exoplayer3.dash)
    implementation(libs.exoplayer3.ui)

    // splash screen
    implementation(libs.splash.screen)

    // navigation
    implementation(libs.jetpack.navigation)

    // datastore
    implementation(libs.jetpack.datastore.preference)

    implementation(libs.kotlin.serialization)

    // adaptive
    implementation(libs.material3.adaptive)
    implementation(libs.material3.adaptive.layout)
    implementation(libs.material3.adaptive.navigation)
    implementation(kotlin("reflect"))
}