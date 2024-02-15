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

package dev.runefox.procsvg

import org.w3c.dom.svg.SVGStylable
import java.io.FileReader
import java.io.FileWriter

fun main() {
    val ctx = processor {
        // Text on badge
        (tagIs("text") and idIs("subtitle")) {
            setTextContent(literally("Funny convention"))
        }
        (tagIs("text") and idIs("name")) {
            setTextContent(literally("Reffurence"))
        }
        (tagIs("text") and idIs("badge_number")) {
            setTextContent(literally("#1"))
        }
        (tagIs("text") and idIs("role")) {
            setTextContent(literally("Convention"))
        }
        (tagIs("text") and idIs("pronouns")) {
            setTextContent(literally("wdym pronouns?"))
        }

        // Colours on badge
        (idIs("name_tag_bg")) {
            (typeIs(SVGStylable::class)) {
                setCss("fill", literally("#FDFF12"))
            }
        }

        // Avatar
        (idIs("avatar")) {
            embedImageHref(literally("testfiles/logo.png"))
        }
    }

    val document = FileReader("testfiles/base.svg").use {
        readDocument(it)
    }

    val copy = newDocument()
    copy.removeChild(copy.documentElement)
    copy.appendChild(copy.importNode(document.documentElement, true))
    copy.process(ctx)

    FileWriter("testfiles/modified.svg").use {
        copy.write(it)
    }
}
