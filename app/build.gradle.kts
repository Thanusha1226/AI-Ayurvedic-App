plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.techno.aiproject"
    compileSdk = 34

    androidResources{
        noCompress.add("tflite")
    }

    defaultConfig {
        applicationId = "com.techno.aiproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
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
    packaging{
        resources{

            excludes +="META-INF/INDEX.LIST"
            pickFirsts +="META-INF/DEPENDENCIES"
            pickFirsts +="META-INF/io.netty.versions.properties"
        }

    }
    buildFeatures {
        mlModelBinding = true
        buildConfig = true
    }

}

dependencies {

    // Room components
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-common-jvm:2.8.4")
    implementation("com.google.firebase:firebase-database:22.0.1")
    implementation("com.google.firebase:firebase-firestore:26.4.1")
    annotationProcessor("androidx.room:room-compiler:2.8.4")

    // ViewModel & LiveData (MVVM)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.fragment:fragment:1.6.2")

    // ViewPager2 (Onboarding)
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-messaging:24.0.2")
    implementation("com.google.firebase:firebase-auth:22.1.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.6.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-tls:4.12.0")

    implementation("io.reactivex.rxjava3:rxjava:3.1.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    implementation("androidx.webkit:webkit:1.8.0")

    implementation ("com.google.firebase:firebase-messaging:23.3.1")

    // Country code plugin
    implementation("net.rimoto:intlphoneinput:1.0.1")

    implementation ("androidx.core:core:1.13.0")

    //tensorflow
    implementation ("org.tensorflow:tensorflow-lite:2.16.1")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.3")
    implementation ("org.tensorflow:tensorflow-lite-task-text:0.4.3")
    // Optional for GPU acceleration
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.16.1")

    //ENCRYPT
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")





}