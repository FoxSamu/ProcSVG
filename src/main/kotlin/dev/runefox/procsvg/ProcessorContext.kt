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
import org.w3c.dom.Text

/**
 * A built context for processing.
 */
class ProcessorContext(

    /**
     * The list of mapped [NodeProcessor]s in the order they are applied. Unlike [processors], these are not inherited.
     * These processors are always applied before [processors] and are generated from [ProcessorDSLModel.otherwise] and
     * [ProcessorDSLModel.where].
     */
    val mappedProcessors: List<NodeProcessor>,

    /**
     * The list of [NodeProcessor]s in the order they are applied.
     */
    val processors: List<NodeProcessor>,

    /**
     * A [PropertyProvider] that provides properties for this context.
     */
    val properties: PropertyProvider,

    /**
     * Whether to traverse the children of processed nodes.
     */
    val traverse: Boolean
) {
    private var continueProcessing = true

    /**
     * Apply this context's [NodeProcessor]s in order to the given node. Note that this directly modifies the node and
     * thus the document. Make a copy of the document if you don't want it to modify the source document instance.
     *
     * @param node The node to process and modify.
     */
    fun applyProcessors(node: Node) {
        continueProcessing = true

        if (continueProcessing)
            for (p in mappedProcessors) {
                p.apply { process(node) }

                if (!continueProcessing)
                    break
            }

        if (continueProcessing)
            for (p in processors) {
                p.apply { process(node) }

                if (!continueProcessing)
                    break
            }

        if ((node is Element || node is Text) && node.parentNode == null)
            return // It was removed from its parent and it's not coming back, we don't need to traverse it anymore

        if (traverse) {
            node.childNodes.apply {
                for (i in 0 until length) {
                    applyProcessors(item(i))
                }
            }
        }
    }

    /**
     * Gets the value of a property with given name.
     */
    operator fun get(name: String) = properties[name] ?: ""

    /**
     * Gets the value of a [Property].
     */
    operator fun get(prop: Property) = prop.apply(this)


    /**
     * Forks the given context and builds a new context using the DSL.
     */
    inline fun fork(action: ProcessorDSLFunction): ProcessorContext {
        return ProcessorDSLModel(this).apply(action).newContext()
    }

    /**
     * Immediately stops processing of the current node in the current context. Further processors will not be applied.
     */
    fun immediatelyStopProcessing() {
        continueProcessing = false
    }
}


typealias ProcessorDSLFunction = ProcessorDSLModel.() -> Unit

/**
 * The processor domain-specific language. Typically this is extended using extension functions, which add
 * [NodeProcessor]s through [process].
 */
class ProcessorDSLModel(private val parent: ProcessorContext?) {

    private val maps = mutableListOf<NodeProcessor>()
    private val processors = mutableListOf<NodeProcessor>()
    private var traverse = parent?.traverse ?: true

    /**
     * Adds a [NodeProcessor].
     */
    fun process(processor: NodeProcessor) {
        processors += processor
    }

    private fun addMap(processor: NodeProcessor) {
        maps += processor
    }

    /**
     * Maps the context by given DSL function. Any [NodeProcessor]s in this context must be inherited into the new
     * child context for them to be applied. An `otherwise` block can follow a bunch of `where` blocks as a kind of
     * `else`, where the only purpose of any parent processors is to be inherited.
     */
    fun otherwise(mapper: ProcessorDSLFunction = {}) =
        addMap {
            fork(mapper).applyProcessors(it)
            immediatelyStopProcessing()
        }

    /**
     * Maps the context by given DSL function if the given [NodeFilter] matches the node. Any [NodeProcessor] added
     * to this context will not be applied to filtered nodes unless explicitly inherited.
     */
    fun where(filter: NodeFilter, mapper: ProcessorDSLFunction = {}) =
        addMap {
            if (filter.appliesTo(it)) {
                fork(mapper).applyProcessors(it)
                immediatelyStopProcessing()
            }
        }

    /**
     * Maps the context by given DSL function if the given [Condition] is true. Any [NodeProcessor] added
     * to this context will not be applied to filtered nodes unless explicitly inherited.
     */
    fun where(filter: Condition, mapper: ProcessorDSLFunction = {}) =
        addMap {
            if (filter.apply(this)) {
                fork(mapper).applyProcessors(it)
                immediatelyStopProcessing()
            }
        }

    /**
     * Equivalent to [where].
     */
    operator fun NodeFilter.invoke(mapper: ProcessorDSLFunction) =
        where(this, mapper)

    /**
     * Equivalent to [where].
     */
    operator fun Condition.invoke(mapper: ProcessorDSLFunction) =
        where(this, mapper)

    /**
     * Inherits all the processors from the parent context into this context.
     */
    fun inherit() {
        parent?.processors?.forEach(this::process)
    }

    /**
     * Creates a new [ProcessorContext], optionally with a new property provider. If the parent has any property
     * provider, it will inherit those properties and lay the given property provider on top. If it has no property
     * provider, it will simply use the given one.
     */
    fun newContext(properties: PropertyProvider? = null): ProcessorContext {
        return ProcessorContext(
            maps.toList(), processors.toList(), when {
                properties != null && parent != null
                -> properties above parent.properties

                properties != null
                -> properties

                parent != null
                -> parent.properties

                else
                -> PropertyProvider.empty
            }, traverse
        )
    }

    /**
     * Indicates that this context should not traverse the child nodes of this node.
     */
    fun stopTraverse() {
        traverse = false
    }
}
