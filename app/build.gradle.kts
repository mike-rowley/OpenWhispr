import java.net.URI
import java.security.MessageDigest

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Native library behind local transcription. The Kotlin wrappers in
// com/k2fsa/sherpa/onnx are copied verbatim from this sherpa-onnx release and
// the JNI library must match them exactly, so bump both together. The
// static-link build has onnxruntime folded into the one .so, so nothing else
// needs shipping. The archive is cached under the root .gradle/ dir so a
// `clean` doesn't re-download it.
val sherpaOnnxVersion = "1.12.28"
val sherpaOnnxSha256 = "56f582b289ef656a70892c6a8e83647b6d25dd92eb1b016c57a7fd64678d10ed"
val sherpaOnnxArchive = rootProject.file(
    ".gradle/sherpa-onnx/sherpa-onnx-v$sherpaOnnxVersion-android-static-link-onnxruntime.tar.bz2"
)
val sherpaOnnxJniLibs = layout.buildDirectory.dir("generated/sherpa-onnx/jniLibs").get().asFile

android {
    namespace = "com.edib.openwhispr"
    compileSdk = 35

    signingConfigs {
        getByName("debug") {
            // Checked-in debug key so every build (local or CI) signs with the
            // same certificate. Without this, each machine/CI run generates
            // its own throwaway debug key, and Android refuses to install an
            // "update" whose signature doesn't match what's already there.
            storeFile = file("../keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.edib.openwhispr"
        minSdk = 30
        targetSdk = 35
        versionCode = 25
        versionName = "3.10.0"

        ndk { abiFilters += "arm64-v8a" }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    @Suppress("DEPRECATION")
    kotlinOptions { jvmTarget = "17" }

    testOptions { unitTests { isIncludeAndroidResources = true } }

    sourceSets["main"].jniLibs.srcDir(sherpaOnnxJniLibs)
}

val downloadSherpaOnnx by tasks.registering {
    val archive = sherpaOnnxArchive
    val expected = sherpaOnnxSha256
    val url = "https://github.com/k2-fsa/sherpa-onnx/releases/download/v$sherpaOnnxVersion/${archive.name}"
    inputs.property("sha256", expected)
    outputs.file(archive)
    doLast {
        fun sha256(file: File): String {
            val md = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { input ->
                val buf = ByteArray(1 shl 16)
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    md.update(buf, 0, n)
                }
            }
            return md.digest().joinToString("") { "%02x".format(it) }
        }

        if (archive.exists() && sha256(archive) == expected) return@doLast
        archive.parentFile.mkdirs()
        val part = File(archive.path + ".part")
        logger.lifecycle("Downloading $url")
        URI(url).toURL().openStream().use { input -> part.outputStream().use { input.copyTo(it) } }
        val actual = sha256(part)
        if (actual != expected) {
            part.delete()
            throw GradleException("sherpa-onnx archive checksum mismatch: expected $expected, got $actual")
        }
        part.renameTo(archive)
    }
}

// Only arm64-v8a, matching ndk.abiFilters above.
val extractSherpaOnnx by tasks.registering(Sync::class) {
    dependsOn(downloadSherpaOnnx)
    from(tarTree(resources.bzip2(sherpaOnnxArchive))) {
        include("**/arm64-v8a/*.so")
        eachFile { path = "arm64-v8a/$name" }
        includeEmptyDirs = false
    }
    into(sherpaOnnxJniLibs)
}

tasks.named("preBuild") { dependsOn(extractSherpaOnnx) }

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.14.0")
    implementation("org.apache.commons:commons-compress:1.27.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}
