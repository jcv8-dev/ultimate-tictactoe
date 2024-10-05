plugins {
    id("java")
}

group = "com.jcvb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // https://mvnrepository.com/artifact/io.github.kostaskougios/cloning
    implementation("io.github.kostaskougios:cloning:1.10.3")

}

tasks.test {
    useJUnitPlatform()
}