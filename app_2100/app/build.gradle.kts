plugins {
    id("com.android.application")

    // Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.app_2100"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.app_2100"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity:1.7.0")
    implementation("androidx.fragment:fragment:1.6.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("junit:junit:4.12")
    implementation("androidx.work:work-runtime:2.7.0")
//    implementation("androidx.camera:camera-core:1.3.3") // for notifications
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    // Dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")

    // Dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    // Dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage")
}