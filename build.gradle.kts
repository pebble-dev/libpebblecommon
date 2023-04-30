import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
}

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    `maven-publish`
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.library")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/pebble-dev/libpebblecommon")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

android {
    namespace = project.group.toString()
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = compileSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(11)
    }
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()

    iosX64("iosX64") { // Simulator
        binaries {
            framework {
                baseName = "libpebblecommon"
            }
        }
    }
    iosArm64("ios") {
        binaries {
            framework {
                baseName = "libpebblecommon"
            }
        }
    }

    iosArm32("iosArmv7") {
        binaries {
            framework {
                baseName = "libpebblecommon"
            }
        }
    }

    iosSimulatorArm64("iosSimulatorArm64") {
        binaries {
            framework {
                baseName = "libpebblecommon"
            }
        }
    }

    val klockVersion = "2.4.13"
    val ktorVersion = "1.6.7"
    val coroutinesVersion = "1.6.4"
    val uuidVersion = "0.4.1"
    val kotlinxSerVersion = "1.5.0"
    val kermitVersion = "2.0.0-RC4"

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalSerializationApi")
            }
        }
        sourceSets["commonMain"].dependencies {
            implementation(kotlin("stdlib-common"))
            implementation("com.benasher44:uuid:$uuidVersion")
            implementation("com.soywiz.korlibs.klock:klock:$klockVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion") {
                version {
                    strictly(coroutinesVersion)
                }
            }
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerVersion")
            implementation("co.touchlab:kermit:$kermitVersion")
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
        }

        sourceSets["androidMain"].dependencies {
        }

        sourceSets["iosMain"].dependencies {
        }

        val iosX64Main by getting {
            kotlin.srcDir("src/iosMain/kotlin")
        }

        val iosArmv7Main by getting {
            kotlin.srcDir("src/iosMain/kotlin")
        }

        val iosSimulatorArm64Main by getting {
            kotlin.srcDir("src/iosMain/kotlin")
        }

        sourceSets["jvmMain"].dependencies {
        }

        sourceSets["jvmTest"].dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
            implementation("io.ktor:ktor-client-websockets:$ktorVersion")
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }
    }
}

if (Os.isFamily(Os.FAMILY_MAC)) {
    val iosSimulatorFatFramework by tasks.registering(PlatformFatFramework::class) {
        onlyIf {
            Os.isFamily(Os.FAMILY_MAC)
        }
        val iosX64Task = (kotlin.targets.getByName("iosX64") as KotlinNativeTarget).binaries.getFramework("RELEASE")
        val iosSimulatorArm64Task = (kotlin.targets.getByName("iosSimulatorArm64") as KotlinNativeTarget).binaries.getFramework("RELEASE")
        dependsOn(iosX64Task.linkTask)
        dependsOn(iosSimulatorArm64Task.linkTask)
        platform.set("simulator")

        inputFrameworks.setFrom(project.files(iosX64Task.outputFile, iosSimulatorArm64Task.outputFile))
        inputFrameworkDSYMs.setFrom(project.files(iosX64Task.outputFile.path+".dSYM", iosX64Task.outputFile.path+".dSYM"))
    }

    val iosDeviceFatFramework by tasks.registering(PlatformFatFramework::class) {
        onlyIf {
            Os.isFamily(Os.FAMILY_MAC)
        }
        val iosTask = (kotlin.targets.getByName("ios") as KotlinNativeTarget).binaries.getFramework("RELEASE")
        val iosArmv7Task = (kotlin.targets.getByName("iosArmv7") as KotlinNativeTarget).binaries.getFramework("RELEASE")
        dependsOn(iosTask.linkTask)
        dependsOn(iosArmv7Task.linkTask)
        platform.set("device")

        inputFrameworks.setFrom(project.files(iosTask.outputFile, iosArmv7Task.outputFile))
        inputFrameworkDSYMs.setFrom(project.files(iosTask.outputFile.path+".dSYM", iosArmv7Task.outputFile.path+".dSYM"))
    }

    val assembleXCFramework by tasks.registering {
        onlyIf {
            org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)
        }
        val deviceTask = tasks.getByName("iosDeviceFatFramework")
        val simulatorTask = tasks.getByName("iosSimulatorFatFramework")
        dependsOn(deviceTask)
        dependsOn(simulatorTask)
        outputs.dir(layout.buildDirectory.dir("xcframework")).withPropertyName("outputDir")

        val outputPath = layout.buildDirectory.dir("xcframework").get().asFile.path + "/libpebblecommon.xcframework"

        doLast {
            delete(outputPath)
            exec {
                commandLine (
                    "xcodebuild", "-create-xcframework",
                    "-framework", deviceTask.outputs.files.first { it.name == "libpebblecommon.framework" }.path,
                    "-debug-symbols", deviceTask.outputs.files.first { it.name == "libpebblecommon.framework.dSYM" }.path,
                    "-framework", simulatorTask.outputs.files.first { it.name == "libpebblecommon.framework" }.path,
                    "-debug-symbols", simulatorTask.outputs.files.first { it.name == "libpebblecommon.framework.dSYM" }.path,
                    "-output", outputPath
                )
            }
        }
    }
}

project.afterEvaluate {
    tasks.withType(PublishToMavenRepository::class.java) {
        onlyIf {
            !publication.name.contains("ios")
        }
    }
    tasks.withType(Jar::class.java) {
        onlyIf {
            !name.contains("ios")
        }
    }
}

abstract class PlatformFatFramework: DefaultTask() {
    @get:Input
    abstract val platform: Property<String>

    @get:InputFiles
    val inputFrameworks = project.objects.fileCollection()

    @get:InputFiles
    val inputFrameworkDSYMs = project.objects.fileCollection()

    @Internal
    val platformOutputDir: Provider<Directory> = platform.map { project.layout.buildDirectory.dir("platform-fat-framework/${it}").get() }

    @get:OutputDirectory
    val outputDir = project.objects.directoryProperty().convention(platformOutputDir)

    @get:OutputDirectories
    val outputFiles: Provider<Array<File>> = platformOutputDir.map {arrayOf(
        it.asFile.toPath().resolve(inputFrameworks.files.first().name).toFile(),
        it.asFile.toPath().resolve(inputFrameworkDSYMs.files.first().name).toFile()
    )}

    private fun copyFramework() {
        val file = inputFrameworks.files.first()
        project.copy {
            from(file)
            into(outputDir.get().asFile.toPath().resolve(file.name))
        }
    }

    private fun copyFrameworkDSYM() {
        val file = inputFrameworkDSYMs.first()
        project.copy {
            from(file)
            into(outputDir.get().asFile.toPath().resolve(file.name))
        }
    }

    private fun lipoMergeFrameworks() {
        val inputs = mutableListOf<String>()
        inputFrameworks.forEach {
            inputs.add(it.toPath().resolve("libpebblecommon").toString())
        }
        val out = outputDir.get().asFile.toPath()
            .resolve(inputFrameworks.files.first().name+"/libpebblecommon").toString()
        project.exec {
            commandLine ("lipo", "-create", *inputs.toTypedArray(), "-output", out)
        }
    }

    private fun lipoMergeFrameworkDSYMs() {
        val inputs = mutableListOf<String>()
        inputFrameworkDSYMs.forEach {
            inputs.add(it.toPath().resolve("Contents/Resources/DWARF/libpebblecommon").toString())
        }
        val out = outputDir.get().asFile.toPath()
            .resolve(inputFrameworkDSYMs.files.first().name+"/Contents/Resources/DWARF/libpebblecommon").toString()
        project.exec {
            commandLine ("lipo", "-create", *inputs.toTypedArray(), "-output", out)
        }
    }

    @TaskAction
    fun createPlatformFatFramework() {
        copyFramework()
        copyFrameworkDSYM()
        lipoMergeFrameworks()
        lipoMergeFrameworkDSYMs()
    }
}