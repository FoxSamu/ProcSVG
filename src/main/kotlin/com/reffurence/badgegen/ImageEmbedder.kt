package com.reffurence.badgegen

import org.w3c.dom.svg.SVGImageElement
import java.io.FileInputStream
import java.io.InputStream
import java.util.Base64

/**
 * Converts the data in an input stream to a base64 data url.
 */
private fun toDataString(stream: InputStream, media: String = "image/png"): String {
    val prefix = "data:$media;base64,"

    return stream.use {
        prefix + Base64.getEncoder().encodeToString(stream.readAllBytes())
    }
}

/**
 * Embeds an image loaded from a file on the file system into the SVG document as the source of an existing `<image>`
 * element, by converting it to a data URL and setting the `xlink:href` property. Only applies to [SVGImageElement]
 * nodes.
 *
 * @param filename A [Property] that locates the file to be embedded.
 * @param media A [Property] that determines the MIME type, by default it's `image/png`.
 * @param fileOpener A function that locates a file name that came out of the property from [filename] and opens it as
 * an [InputStream].
 */
fun ProcessorDSL.embedImageHref(
    filename: Property,
    media: Property = literally("image/png"),
    fileOpener: (String) -> InputStream = { FileInputStream(it) }
) {
    add {
        if (it is SVGImageElement) {
            it.href.baseVal = toDataString(fileOpener(this[filename]), this[media])
        }
    }
}
