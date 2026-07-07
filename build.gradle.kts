import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

plugins {
	java
}

// Independent plugin version — keep in sync with StringDecryptPlugin.VERSION (see AGENT.md).
group = "io.github.arsylk"
version = "1.13.0"
description = "jadx plugin: compile-time constant deobfuscator + resolvable block-cipher string decryption"

repositories {
	mavenCentral()
	google() // jadx-dex-input (test only) pulls com.android.tools.smali:* from Google's Maven
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	// jadx-core is provided by the host jadx at runtime; never bundle it.
	// Bump together with REQUIRED_JADX_VERSION in StringDecryptPlugin.
	compileOnly("io.github.skylot:jadx-core:1.5.2")
	compileOnly("org.slf4j:slf4j-api:2.0.17")
	compileOnly("org.jetbrains:annotations:26.0.2")

	// Tests run the plugin against REAL obfuscated APKs (see RealApkDeobfTestBase): the released
	// jadx-core + dex-input are pulled from Maven Central so the suite exercises the plugin exactly
	// as it ships standalone (decoupled from the local jadx working tree). Keep these pinned to the
	// same version as the compileOnly jadx-core above.
	testImplementation("io.github.skylot:jadx-core:1.5.2")
	testImplementation("io.github.skylot:jadx-dex-input:1.5.2")
	testImplementation("org.jetbrains:annotations:26.0.2")
	testImplementation("org.junit.jupiter:junit-jupiter:5.13.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.assertj:assertj-core:3.27.7")
	testRuntimeOnly("ch.qos.logback:logback-classic:1.5.32")
}

tasks.test {
	useJUnitPlatform()
	// integration tests decompile a whole APK each — never serve a stale cached result
	outputs.cacheIf { false }
	// `-DupdateGolden=true` regenerates the golden fixtures instead of asserting against them
	systemProperty("updateGolden", System.getProperty("updateGolden", "false"))
	testLogging {
		showStandardStreams = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
}

// Generate BuildInfo.java with the version + UTC build timestamp. Surfaced through
// StringDecryptOptions as a read-only "build-time" entry in jadx-gui's settings panel so users
// can tell which jar is currently loaded.
val buildInfoDir = layout.buildDirectory.dir("generated/sources/buildInfo/java/main")
val generateBuildInfo by tasks.registering {
	val outDir = buildInfoDir
	outputs.dir(outDir)
	inputs.property("version", project.version)
	// Always re-run so BUILD_TIME truly reflects when the jar was built (otherwise gradle would
	// up-to-date-check on inputs alone and keep the previous timestamp across source-only edits).
	outputs.upToDateWhen { false }
	doLast {
		val pkgDir = outDir.get().asFile.resolve("jadx/plugins/stringdecrypt")
		pkgDir.mkdirs()
		val ts = OffsetDateTime.now(ZoneOffset.UTC)
				.truncatedTo(ChronoUnit.SECONDS)
				.toString()
		pkgDir.resolve("BuildInfo.java").writeText(
			"""
			package jadx.plugins.stringdecrypt;

			// AUTO-GENERATED at build time. DO NOT EDIT.
			public final class BuildInfo {
				public static final String VERSION = "${project.version}";
				public static final String BUILD_TIME = "$ts";

				private BuildInfo() {
				}
			}
			""".trimIndent() + "\n",
		)
	}
}
sourceSets.main.get().java.srcDir(buildInfoDir)
tasks.compileJava { dependsOn(generateBuildInfo) }

tasks {
	compileJava {
		options.encoding = "UTF-8"
	}
	jar {
		manifest {
			attributes(
				"Implementation-Title" to "jadx-string-decrypt",
				"Implementation-Version" to project.version,
				"Plugin-Id" to "string-decrypt",
				"Plugin-Name" to "Constant Deobfuscator",
			)
		}
	}
}
