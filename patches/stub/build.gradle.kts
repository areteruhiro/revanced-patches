plugins {
    id('com.android.library')
    id('org.jetbrains.kotlin.android')
}

android {
    compileSdkVersion 34
    buildToolsVersion "34.0.0"

    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 34
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = '11'
    }
}

dependencies {
    // ReVancedコア依存関係
    implementation 'app.revanced:patcher:7.0.0'
    implementation 'app.revanced:library:5.0.0'
    
    // Androidサポートライブラリ
    compileOnly 'com.android.tools.build:gradle:8.1.0'
    compileOnly 'com.android.tools:common:31.0.0'
    
    // Kotlinサポート
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0'
    
    // Smali解析
    implementation 'org.smali:smali:2.5.2'
    implementation 'org.smali:dexlib2:2.5.2'
}

repositories {
    mavenCentral()
    google()
}
