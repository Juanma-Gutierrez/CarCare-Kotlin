import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    // Kapt
    id("org.jetbrains.kotlin.kapt")
    // Dokka
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.juanmaGutierrez.carcare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.juanmaGutierrez.carcare"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // ViewBinding
    buildFeatures {
        viewBinding = true
    }

    // Dokka config
    tasks.dokkaHtml.configure {
        dokkaSourceSets {
            configureEach {
                outputDirectory.set(layout.buildDirectory.dir("./documentation/html"))
                suppressObviousFunctions.set(true)
                suppressInheritedMembers.set(true)
                documentedVisibilities.set(
                    listOf(org.jetbrains.dokka.DokkaConfiguration.Visibility.PRIVATE)
                            + documentedVisibilities.get()
                )
            }
        }
    }
}

dependencies {
    // ViewModels in fragments
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.storage.ktx)
    // Retrofit
    val rfVersion = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$rfVersion")
    implementation("com.squareup.retrofit2:converter-gson:$rfVersion")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Add the dependency for the Firebase Firestore
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")
    // Add the dependency for the Firebase Cloud Storage
    implementation("com.google.firebase:firebase-storage")
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Room
    val roomVersion = "2.6.0"
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Lottie
    val lottieVersion = "6.3.0"
    implementation("com.airbnb.android:lottie:$lottieVersion")
    // Camera
    val cameraVersion = "1.3.3"
    implementation("androidx.camera:camera-core:$cameraVersion")
    implementation("androidx.camera:camera-camera2:$cameraVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraVersion")
    implementation("androidx.camera:camera-video:$cameraVersion")
    implementation("androidx.camera:camera-view:$cameraVersion")
    implementation("androidx.camera:camera-extensions:$cameraVersion")
    // Coil
    val coilVersion = "2.5.0"
    implementation("io.coil-kt:coil:$coilVersion")
    // WilliamChart
    val williamChartVersion = "3.10.1"
    implementation("com.diogobernardino:williamchart:$williamChartVersion")
    // SplashScreen
    val splashScreenVersion = "1.0.0"
    implementation("androidx.core:core-splashscreen:$splashScreenVersion")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}