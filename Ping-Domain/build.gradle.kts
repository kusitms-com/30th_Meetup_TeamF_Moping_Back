dependencies {
    implementation(project(":Ping-Common"))
}
tasks {
    bootJar {
        isEnabled = false
    }
    jar {
        isEnabled = true
    }
}