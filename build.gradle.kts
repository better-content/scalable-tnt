plugins {
    id("net.minecraftforge.gradle") version "6.0.54"
    id("org.spongepowered.mixin") version "0.7.38"
}

val minecraftVersion = property("minecraft_version") as String
val forgeVersion = property("forge_version") as String
val modId = property("mod_id") as String
val modName = property("mod_name") as String
val modVersion = property("mod_version") as String

version = modVersion
group = property("mod_group") as String
base { archivesName.set(property("artifact_name") as String) }

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

minecraft {
    mappings("official", minecraftVersion)
    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.console.level", "info")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
        create("client")
        create("server") { arg("--nogui") }
        create("gameTestServer") {
            workingDirectory(project.file("run-gametest"))
            property("forge.enableGameTest", "true")
            property("forge.gameTestServer", "true")
            property("forge.enabledGameTestNamespaces", modId)
            arg("--nogui")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
}

mixin { config("scalable_tnt.mixins.json") }

tasks.named<Jar>("jar") { finalizedBy("reobfJar") }
val stageRuntimeJar by tasks.registering(Copy::class) {
    dependsOn(tasks.named("reobfJar"))
    from(layout.buildDirectory.file("reobfJar/output.jar"))
    into(layout.buildDirectory.dir("libs"))
    rename { "${base.archivesName.get()}-$version.jar" }
}
tasks.named("assemble") { dependsOn(stageRuntimeJar) }
val syncGameTestStructures by tasks.registering(Copy::class) {
    from("src/main/resources/gameteststructures")
    into("run-gametest/gameteststructures")
}
tasks.matching { it.name.startsWith("prepareRunGameTestServer") }.configureEach {
    dependsOn(syncGameTestStructures)
}
var gameTestStartedAt = 0L
tasks.matching { it.name == "runGameTestServer" }.configureEach {
    doFirst { gameTestStartedAt = System.currentTimeMillis() }
    doLast {
        val log = layout.projectDirectory.file("run-gametest/logs/latest.log").asFile
        if (!log.isFile || log.lastModified() < gameTestStartedAt) {
            throw GradleException("GameTest server returned without a fresh execution log")
        }
        val report = log.readText()
        if (!report.contains("1 tests are now running!")
            || !report.contains("1 GAME TESTS COMPLETE")
            || !report.contains("All 1 required tests passed :)")) {
            throw GradleException("Scalable TNT GameTest did not execute and pass its required profile")
        }
    }
}
tasks.register("verifyFast") {
    group = "verification"
    dependsOn(tasks.named("check"))
}
tasks.register("verifyFull") {
    group = "verification"
    dependsOn(tasks.named("verifyFast"), tasks.named("runGameTestServer"))
}

tasks.processResources {
    val props = mapOf(
        "minecraftVersion" to minecraftVersion,
        "forgeVersion" to forgeVersion,
        "modId" to modId,
        "modName" to modName,
        "modVersion" to modVersion,
    )
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") { expand(props) }
}

tasks.withType<JavaCompile>().configureEach { options.release.set(17) }
tasks.withType<Test>().configureEach { useJUnitPlatform() }
