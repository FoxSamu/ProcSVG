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

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGStylable
import kotlin.reflect.KClass

/**
 * A processor of a W3C DOM [Node].
 */
fun interface NodeProcessor {
    fun ProcessorContext.process(node: Node)
}

/**
 * A filter on a W3C DOM [Node].
 */
fun interface NodeFilter {
    fun appliesTo(node: Node): Boolean
}

/**
 * A [NodeFilter] that tests whether the [Node] instance extends/implements a certain class.
 */
fun typeIs(cls: KClass<*>) =
    NodeFilter { cls.isInstance(it) }

/**
 * A [NodeFilter] that tests whether the [Node] is an [Element] with given tag name.
 * It must be an [Element] for this filter to pass.
 */
fun tagIs(name: String) =
    NodeFilter { it is Element && it.tagName == name }

/**
 * A [NodeFilter] that tests whether the [Node] is an [SVGElement] with given ID (the `id` attribute).
 * It must be an [SVGElement] for this filter to pass.
 */
fun idIs(name: String) =
    NodeFilter { it is SVGElement && it.id == name }

/**
 * A [NodeFilter] that tests whether the [Node] is an [Element] that has the given attribute (with any value).
 * It must be an [Element] for this filter to pass.
 */
fun hasAttr(name: String) =
    NodeFilter { it is Element && it.hasAttribute(name) }

/**
 * A [NodeFilter] that tests whether the [Node] is an [Element] that has the given attribute with given value.
 * It must be an [Element] for this filter to pass.
 */
fun hasAttr(name: String, value: String) =
    NodeFilter { it is Element && it.hasAttribute(name) && it.getAttribute(name) == value }

/**
 * A [NodeFilter] that tests whether the [Node] is an [Element] that has the given name in its
 * [`className`](SVGStylable.className). It must be [SVGStylable] for this filter to pass.
 */
fun hasClass(name: String) =
    NodeFilter { it is SVGStylable && name in it.className.baseVal.split(" +") }

/**
 * A [NodeFilter] that checks the given [NodeFilter] on the direct parent of the filtered node.
 */
fun parentMatches(filter: NodeFilter) =
    NodeFilter { filter.appliesTo(it.parentNode) }

/**
 * A [NodeFilter] that passes if the given [NodeFilter] passes on one or more of the parents of the filtered node.
 */
fun anyParentMatches(filter: NodeFilter) =
    NodeFilter {
        var n = it.parentNode
        while (n != null) {
            if (filter.appliesTo(n))
                return@NodeFilter true
            n = n.parentNode
        }

        false
    }

/**
 * A [NodeFilter] that matches anything.
 */
val any =
    NodeFilter { true }

/**
 * Constructs a conjunction of the left [NodeFilter] and the right [NodeFilter], i.e. it returns a filter that only
 * passes if both of the given nodes pass.
 */
infix fun NodeFilter.and(other: NodeFilter) =
    NodeFilter { appliesTo(it) && other.appliesTo(it) }

/**
 * Constructs a disjunction of the left [NodeFilter] and the right [NodeFilter], i.e. it returns a filter that
 * passes if either or both of the given nodes pass.
 */
infix fun NodeFilter.or(other: NodeFilter) =
    NodeFilter { appliesTo(it) || other.appliesTo(it) }


/**
 * Applies a processor that simply removes the node and its children from the document.
 */
fun ProcessorDSLModel.removeNode() =
    process { it.parentNode.removeChild(it) }

/**
 * Applies a processor that replaces the children of the node with a simple text node with given text.
 * @param text A [Property] that defines the new text content of this node.
 */
fun ProcessorDSLModel.setTextContent(text: Property) {
    process { it.textContent = this[text] }
    stopTraverse()
}

/**
 * Applies a processor that replaces the value of an attribute of this node with given text. Only targets nodes of type
 * [Element].
 * @param attribute A [Property] that defines the name of the attribute to change.
 * @param value A [Property] that defines the new attribute value.
 */
fun ProcessorDSLModel.setAttr(attribute: Property, value: Property) =
    process {
        if (it is Element) {
            it.setAttribute(this[attribute], this[value])
        }
    }

/**
 * Applies a processor that removes an attribute from this node. Only targets nodes of type [Element].
 * @param attribute A [Property] that defines the name of the attribute to remove.
 */
fun ProcessorDSLModel.removeAttr(attribute: Property) =
    process {
        if (it is Element) {
            it.removeAttribute(this[attribute])
        }
    }

/**
 * Applies a processor that replaces the value of a CSS property of this node with given value. Only targets nodes of
 * type [SVGStylable].
 * @param property A [Property] that defines the name of the property to change.
 * @param value A [Property] that defines the new property value.
 */
fun ProcessorDSLModel.setCss(property: Property, value: Property) =
    process {
        if (it is SVGStylable) {
            it.style.setProperty(this[property], this[value], "")
        }
    }


/**
 * Applies a processor that replaces the value of an attribute of this node with given text. Only targets nodes of type [Element].
 * @param attribute The name of the attribute to change.
 * @param value A [Property] that defines the new attribute value.
 */
fun ProcessorDSLModel.setAttr(attribute: String, value: Property) =
    process {
        if (it is Element) {
            it.setAttribute(attribute, this[value])
        }
    }

/**
 * Applies a processor that removes an attribute from this node. Only targets nodes of type [Element].
 * @param attribute The name of the attribute to remove.
 */
fun ProcessorDSLModel.removeAttr(attribute: String) =
    process {
        if (it is Element) {
            it.removeAttribute(attribute)
        }
    }

/**
 * Applies a processor that replaces the value of a CSS property of this node with given value. Only targets nodes
 * of type [SVGStylable].
 *
 * @param property The name of the property to change.
 * @param value A [Property] that defines the new property value.
 */
fun ProcessorDSLModel.setCss(property: String, value: Property) =
    process {
        if (it is SVGStylable) {
            it.style.setProperty(property, this[value], "")
        }
    }


/**
 * Applies a processor that simply prints the short Java class name of the [Node] instance. This could be used for
 * debugging.
 */
fun ProcessorDSLModel.printNodeType() =
    process {
        println(it::class.simpleName)
    }
