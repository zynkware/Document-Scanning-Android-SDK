plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.zynksoftware.documentscannersample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zynksoftware.documentscannersample"
        minSdk = 23
        targetSdk = 34
        versionCode = 8
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["enableCrashReporting"] = "false"
        }
        getByName("release") {
            manifestPlaceholders["enableCrashReporting"] = "true"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.github.fondesa:kpermissions:3.3.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation(project(":DocumentScanner"))
}
