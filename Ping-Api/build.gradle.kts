dependencies {
    implementation(project(":Ping-Application"))
    implementation(project(":Ping-Common"))
    implementation(project(":Ping-Domain"))
    implementation(project(":Ping-Infra"))
    implementation(project(":Ping-Client"))

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}
tasks {
    bootJar {
        isEnabled = true
    }
    jar {
        isEnabled = true
    }
}