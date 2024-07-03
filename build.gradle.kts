plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("com.diamondfire.helpbot.HelpBot")
}

group = "com.diamondfire.helpbot"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    implementation ("net.dv8tion:JDA:5.0.0-beta.20") {
        exclude(module = "opus-java")
    }

    implementation("club.minnced:discord-webhooks:0.5.8")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation ("com.github.Steveice10:MCProtocolLib:c5e4b66")
    implementation("mysql:mysql-connector-java:5.1.13")
    implementation("org.codehaus.groovy:groovy-jsr223:3.0.8")
    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    implementation("dev.vankka:mcdiscordreserializer:4.3.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        manifest {
            attributes["Main-Class"] = "com.diamondfire.helpbot.HelpBot"
        }
    }

    build {
        dependsOn(shadowJar)
    }
}