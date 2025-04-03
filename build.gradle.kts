plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "com.samuel.zuo"
version = "0.1.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    // https://mvnrepository.com/artifact/org.pegdown/pegdown
    implementation("org.pegdown:pegdown:1.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.github.java-diff-utils:java-diff-utils:4.12")
    intellijPlatform {
        create("IC", "2024.3")
        bundledPlugin("com.intellij.java")
        instrumentationTools()
        pluginVerifier()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("222")
            untilBuild.set("243.*")
        }
    }
    signing {
        certificateChainFile.set(file("Users/samuel_zuo/.ssh/chain.crt"))
        privateKeyFile.set(file("Users/samuel_zuo/.ssh/private.pem"))
        password.set("qazw")
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
