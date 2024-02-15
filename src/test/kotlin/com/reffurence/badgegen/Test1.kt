package com.reffurence.badgegen

import org.w3c.dom.svg.SVGStylable
import java.io.FileReader
import java.io.FileWriter

fun main() {
    val ctx = context {
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
            embedImageHref(literally("logo.png"))
        }
    }

    val document = FileReader("badge5-base.svg").use {
        readDocument(it)
    }

    val copy = newDocument()
    copy.removeChild(copy.documentElement)
    copy.appendChild(copy.importNode(document.documentElement, true))
    copy.process(ctx)

    FileWriter("modified.svg").use {
        copy.write(it)
    }
}
