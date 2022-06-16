plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.diamondfire.helpbot"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")

    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/maven-releases/")
}

dependencies {
    implementation("net.dv8tion:JDA:4.4.0_352") {
        exclude(module = "opus-java")
    }

    implementation("club.minnced:discord-webhooks:0.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.github.steveice10:mcprotocollib:1.18.2-1")
    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("org.codehaus.groovy:groovy-jsr223:3.0.11")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("com.diamondfire.helpbot.HelpBot")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
