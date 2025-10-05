plugins {
    `kotlin-dsl`
    groovy
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(localGroovy())
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
}

