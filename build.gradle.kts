group = "com.backwardsnode"
version = "2.0.0-ALPHA"
description = "Survival Games Bukkit Plugin"
java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

plugins {
    java
    //`maven-publish`
}

defaultTasks("build")

repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/nexus/repository/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.jetbrains:annotations:23.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testCompileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ProcessResources> {
    filesMatching("*.yml") {
        expand(
            mapOf(
                "version" to project.version,
                // workaround
                "d" to "\$d"
            )
        )
    }
}

tasks.withType<Jar> {
    archiveFileName.set("SurvivalGames.jar")
}

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}
