package com.reffurence.badgegen

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.dom.AbstractStylableDocument
import org.apache.batik.dom.ExtensibleDOMImplementation
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.dom.Document
import java.io.Reader
import java.io.Writer
import javax.xml.transform.Result
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


/**
 * Starts building a context with the processor DSL.
 *
 * ```kotlin
 * context {
 *     // ... your processors here
 * }
 * ```
 *
 * To process a [Document] see [process].
 */
fun context(mapper: ProcessorDSLFunction): ProcessorDSL {
    return ProcessorDSL(null).apply(mapper)
}

/**
 * Processes given [Document], modifying this document instance directly. If that's not desired, make a [copy] first.
 */
fun Document.process(context: ProcessorDSL, properties: PropertyProvider = PropertyProvider.empty) {
    context.newContext(properties).applyProcessors(this)
}

/**
 * Makes a new SVG [Document] with same content as this [Document].
 */
fun Document.copy(): Document {
    val copy = newDocument()
    copy.removeChild(copy.documentElement)
    copy.appendChild(copy.importNode(documentElement, true))
    return copy
}

/**
 * Writes the given [Document] as XML to a [Writer].
 */
fun Document.write(writer: Writer) {
    val transformer = TransformerFactory.newInstance().newTransformer()
    val output: Result = StreamResult(writer)
    transformer.transform(DOMSource(this), output)
}

/**
 * Parses a SVG [Document] from a [Reader].
 */
fun readDocument(reader: Reader): Document {
    val parser = XMLResourceDescriptor.getXMLParserClassName()
    val factory = SAXSVGDocumentFactory(parser)
    return factory.createSVGDocument("file", reader)
}

/**
 * Creates a new SVG [Document].
 */
fun newDocument(): Document {
    val impl = SVGDOMImplementation.getDOMImplementation()
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    val doc = impl.createDocument(svgNS, "svg", null)

    if (impl is ExtensibleDOMImplementation && doc is AbstractStylableDocument) {
        impl.createCSSEngine(doc, BridgeContext(UserAgentAdapter()))
    }

    return doc
}

