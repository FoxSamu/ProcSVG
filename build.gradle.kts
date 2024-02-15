/*
 * Copyright (C) 2024 Samū
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    kotlin("jvm") version "1.9.21"
}

group = "com.reffurence"
version = "0.1"

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
