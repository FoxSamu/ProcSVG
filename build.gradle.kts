plugins {
    kotlin("jvm") version "1.9.21"
}

group = "com.reffurence"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation("org.apache.xmlgraphics:batik-anim:1.17")
    implementation("org.apache.xmlgraphics:batik-awt-util:1.17")
    implementation("org.apache.xmlgraphics:batik-bridge:1.17")
    implementation("org.apache.xmlgraphics:batik-css:1.17")
    implementation("org.apache.xmlgraphics:batik-dom:1.17")
    implementation("org.apache.xmlgraphics:batik-ext:1.17")
    implementation("org.apache.xmlgraphics:batik-gvt:1.17")
    implementation("org.apache.xmlgraphics:batik-parser:1.17")
    implementation("org.apache.xmlgraphics:batik-script:1.17")
    implementation("org.apache.xmlgraphics:batik-svg-dom:1.17")
    implementation("org.apache.xmlgraphics:batik-svggen:1.17")
    implementation("org.apache.xmlgraphics:batik-util:1.17")
    implementation("org.apache.xmlgraphics:batik-xml:1.17")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
