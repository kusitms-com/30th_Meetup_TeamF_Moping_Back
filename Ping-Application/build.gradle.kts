dependencies {
    implementation(project(":Ping-Common"))
    implementation(project(":Ping-Domain"))
    implementation(project(":Ping-Client"))

    //jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
tasks {
    bootJar {
        isEnabled = true
    }
    jar {
        isEnabled = true
    }
}