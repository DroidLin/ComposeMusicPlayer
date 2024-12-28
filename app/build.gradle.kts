plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.dev.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.google.gms.service)
//    alias(libs.plugins.google.firebase.cashlytics)
//    alias(libs.plugins.google.firebase.performance)
}

android {
    namespace = "com.music.android.lin"
    compileSdk = 35
    buildToolsVersion = "34.0.0"
    defaultConfig {
        applicationId = "com.music.android.lin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["debug"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            signingConfig = signingConfigs["debug"]
        }
    }
    signingConfigs {
        maybeCreate("debug").apply {
            this.storeFile = file("keystore/debugKey")
            this.keyAlias = "musicplayer"
            this.keyPassword = "123456"
            this.storePassword = "123456"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        aidl = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
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
    implementation(libs.androidx.material.icon.extended)
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
    implementation(libs.coil.transform)

    // glide
//    implementation(libs.bumptech.glide)
//    implementation(libs.bumptech.glide.extensions)

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

    // kotlin
    implementation(libs.kotlin.serialization)
    implementation(libs.kotin.immutable.collection)

    // adaptive
    implementation(libs.material3.adaptive)
    implementation(libs.material3.adaptive.layout)
    implementation(libs.material3.adaptive.navigation)

    // palette
    implementation(libs.palette)

    // google
//    implementation(platform(libs.google.firebase.bom))
//    implementation(libs.google.firebase.crashlytics)
//    implementation(libs.google.firebase.analytics)
//    implementation(libs.google.firebase.performance)

    // lottie
    implementation(libs.airbnb.lottie)

    // glance
    implementation(libs.glance.widget)
    implementation(libs.glance.material3)
}