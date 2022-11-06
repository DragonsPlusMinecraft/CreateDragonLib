# Depend on it
```
repositories {
    maven {
        // Maven of dragons.plus
        url "https://maven.dragons.plus/Releases"
    }
}

dependencies {
    implementation fg.deobf("plus.dragons.createdragonlib:create_dragon_lib-${minecraft_version}:${create_dragon_lib_version}")
}
```