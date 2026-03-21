import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.closureOf

plugins {
    kotlin("jvm") version "2.2.21"
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.5.0"
}

val modVersion = when (project.findProperty("tr_energy_version") as? String) {
    "2.3.0" -> "${project.property("mod_version")}.180"
    "3.0.0" -> "${project.property("mod_version")}.201"
    "4.1.0" -> "${project.property("mod_version")}.210"
    "4.2.0" -> "${project.property("mod_version")}.215"
    else -> project.property("mod_version")
}
version = modVersion!!

group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 17
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("simplecables76") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    maven(url = "https://maven.pitan76.net/")
    maven(url = "https://maven.shedaniel.me/")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("net.pitan76:mcpitanlib-fabric-${project.property("mcpitanlib_version")}")

    // TRのエネルギー用
    modImplementation("RebornCore:RebornCore-1.20:5.10.3")
    modImplementation("TechReborn:TechReborn-1.20:5.10.3")

    /*
    Version 2.x should be used for Minecraft 1.18-1.19.2 -> 2.3.0
    Version 3.x should be used for Minecraft 1.20.1-1.20.4 -> 3.0.0
    Version 4.1.x should be used for Minecraft 1.20-1.21.4 -> 4.1.0
    Version 4.2.x should be used for Minecraft 1.21.5-1.21.11 -> 4.2.0
     */
    modApi("teamreborn:energy:${project.property("tr_energy_version")}")
    include("teamreborn:energy:${project.property("tr_energy_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {

    }
}

if (System.getenv("CURSEFORGE_TOKEN") != null) {
    curseforge {
        apiKey = System.getenv("CURSEFORGE_TOKEN")
        project(closureOf<CurseProject> {
            id = "1491488"
            changelog = project.property("changelog") as String + "\nMCPitanLib version: " + (project.property("mcpitanlib_version") as String).split(":")[1]
            releaseType = "release"

            if (project.property("tr_energy_version") as String == "2.3.0") {
                gameVersionStrings.addAll(listOf("1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2"))
            }

            if (project.property("tr_energy_version") as String == "3.0.0") {
                gameVersionStrings.addAll(listOf("1.20.1", "1.20.3", "1.20.4"))
            }

            if (project.property("tr_energy_version") as String == "4.1.0") {
                gameVersionStrings.addAll(listOf("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4"))
            }

            if (project.property("tr_energy_version") as String == "4.2.0") {
                gameVersionStrings.addAll(listOf("1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"))
            }

            addGameVersion("Fabric")
            mainArtifact(tasks.named("remapJar"))

            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
                requiredDependency("mcpitanlibarch")
                requiredDependency("fabric-language-kotlin")
            })
        })
    }
}

if (System.getenv("MODRINTH_TOKEN") != null) {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("simplecables76")
        versionNumber.set(project.property("mod_version") as String + "-fabric")

        if (project.property("tr_energy_version") as String == "2.3.0") {
            gameVersions.set(listOf("1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2"))
        }

        if (project.property("tr_energy_version") as String == "3.0.0") {
            gameVersions.set(listOf("1.20.1", "1.20.3", "1.20.4"))
        }

        if (project.property("tr_energy_version") as String == "4.1.0") {
            gameVersions.set(listOf("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4"))
        }

        if (project.property("tr_energy_version") as String == "4.2.0") {
            gameVersions.set(listOf("1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"))
        }

        versionType.set("release")
        uploadFile.set(tasks.named("remapJar"))
        changelog.set(project.property("changelog") as String + "\nMCPitanLib version: " + (project.property("mcpitanlib_version") as String).split(":")[1])
        loaders.set(listOf("fabric"))

        dependencies {
            required.project("P7dR8mSH") // Fabric API
            required.project("uNRoUnGT")
            required.project("fabric-language-kotlin")
        }
    }
}
