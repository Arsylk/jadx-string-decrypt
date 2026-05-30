plugins {
	java
}

// Independent plugin version — keep in sync with StringDecryptPlugin.VERSION (see AGENT.md).
group = "io.github.arsylk"
version = "1.0.1"
description = "jadx plugin: compile-time constant deobfuscator + resolvable block-cipher string decryption"

repositories {
	mavenCentral()
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
}

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
