plugins {
    `java-test-fixtures`

    id("org.asciidoctor.jvm.convert") version "3.3.2"

}

val asciidoctorExt = "asciidoctorExt"
configurations.create(asciidoctorExt) {
    extendsFrom(configurations.testImplementation.get())
}


dependencies {
    implementation(project(":Ping-Application"))
    implementation(project(":Ping-Common"))
    implementation(project(":Ping-Domain"))
    implementation(project(":Ping-Infra"))
    implementation(project(":Ping-Client"))

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    //RestDocs
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.2")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
    testFixturesImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}
tasks {
    bootJar {
        isEnabled = true
    }
    jar {
        isEnabled = true
    }
}

val snippetDir = file("build/generated-snippets")

tasks.asciidoctor {
    inputs.dir(snippetDir)
    dependsOn(tasks.test)
    configurations(asciidoctorExt)
    baseDirFollowsSourceFile()
}
tasks.bootJar{
    dependsOn(tasks.asciidoctor)
    from("build/docs/asciidoc"){
        into("static/docs")
    }
}

tasks.register("copyDocs", Copy::class){
    dependsOn(tasks.bootJar)
    from("build/docs/asciidoc")
    into("src/main/resources/static/docs")
}

tasks.build {
    dependsOn(tasks.getByName("copyDocs"))
}