plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
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


        packagingOptions {
            resources.excludes.add("META-INF/NOTICE.md")
            resources.excludes.add("META-INF/LICENSE.md")
            resources.excludes.add("META-INF/DEPENDENCIES")
        }

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //image-slider++++++++
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation ("androidx.cardview:cardview:1.0.0")
    //++++++++++++++++++++

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //email++++++++++++++
    implementation ("com.google.api-client:google-api-client-android:1.30.10")
    implementation ("com.google.oauth-client:google-oauth-client:1.31.0")
    implementation ("com.google.http-client:google-http-client-android:1.38.0")
    implementation ("com.google.api-client:google-api-client-gson:1.30.10")
    implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.http-client:google-http-client-gson:1.38.0")
    //datePicker+++++++++++++
    implementation ("com.github.prolificinteractive:material-calendarview:2.0.0") {
        exclude ("org.threeten", "threetenbp")
    }
    implementation ("org.threeten:threetenbp:1.5.1")

    //email++++++++++++++++++
    implementation ("com.squareup.picasso:picasso:2.71828")
}