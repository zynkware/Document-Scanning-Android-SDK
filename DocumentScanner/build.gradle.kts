plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.zynksoftware.documentscanner"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
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

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
            res.srcDirs("src/main/res")
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

tasks.register<Jar>("sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    from(fileTree(mapOf("dir" to "src/libs", "include" to listOf("*.jar"))))
    archiveClassifier.set("sources")
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {
                from(components["release"])
                groupId = "com.github.hazzatur"
                artifactId = "Document-Scanning-Android-SDK"
                version = "1.1.3"
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    implementation("com.github.zynkware:Tiny-OpenCV:4.4.0-4")

    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    implementation("com.github.fondesa:kpermissions:3.3.0")

    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("id.zelory:compressor:3.0.1")
    implementation("androidx.test:monitor:1.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    androidTestImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}
