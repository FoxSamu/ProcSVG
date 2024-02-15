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

import java.util.regex.Pattern

/**
 * A simple string value that comes from some configuration.
 */
fun interface Property {
    fun apply(context: ProcessorContext): String
}

/**
 * A simple boolean value that comes from some configuration.
 */
fun interface Condition {
    fun apply(context: ProcessorContext): Boolean
}

/**
 * A [Property] whose value is literally the given string.
 */
fun literally(text: String) = Property { text }

/**
 * A [Property] whose value comes from the [PropertyProvider] of a context.
 */
fun property(name: String) = Property { it[name] }

/**
 * A [Condition] that checks if the left property value equals that of the right property.
 */
infix fun Property.eq(value: Property) = Condition { apply(it).equals(value.apply(it)) }

/**
 * A [Condition] that checks if the left property value equals a given string.
 */
infix fun Property.eq(value: String) = Condition { apply(it).equals(value) }

/**
 * A [Condition] that checks if the left property value does not equal that of the right property.
 */
infix fun Property.neq(value: Property) = Condition { !apply(it).equals(value.apply(it)) }

/**
 * A [Condition] that checks if the left property value does not equal a given string.
 */
infix fun Property.neq(value: String) = Condition { !apply(it).equals(value) }



/**
 * A [Condition] that checks if the left property value equals that of the right property, ignoring case.
 */
infix fun Property.ceq(value: Property) = Condition { apply(it).equals(value.apply(it), false) }

/**
 * A [Condition] that checks if the left property value equals a given string, ignoring case.
 */
infix fun Property.ceq(value: String) = Condition { apply(it).equals(value, false) }

/**
 * A [Condition] that checks if the left property value does not equal that of the right property, ignoring case.
 */
infix fun Property.nceq(value: Property) = Condition { !apply(it).equals(value.apply(it), false) }

/**
 * A [Condition] that checks if the left property value does not equal a given string, ignoring case.
 */
infix fun Property.nceq(value: String) = Condition { !apply(it).equals(value, false) }



/**
 * A [Condition] that checks if the left property value matches a [Pattern].
 */
infix fun Property.matches(value: Pattern) = Condition { value.asMatchPredicate().test(apply(it)) }

/**
 * A [Condition] that checks if the left property value matches a [Pattern].
 */
infix fun Property.matches(value: String) = this matches Pattern.compile(value)

/**
 * Creates a [Property] that is the concatenation of two properties.
 */
operator fun Property.plus(other: Property) = Property { apply(it) + other.apply(it) }

/**
 * Creates a [Condition] that is the conjunction of two conditions.
 */
infix fun Condition.and(other: Condition) = Condition { apply(it) && other.apply(it) }

/**
 * Creates a [Condition] that is the disjunction of two conditions.
 */
infix fun Condition.or(other: Condition) = Condition { apply(it) || other.apply(it) }

/**
 * Creates a [Condition] that is the inverse of a condition.
 */
operator fun Condition.not() = Condition { !apply(it) }

/**
 * Creates a [Condition] that is true if and only if a property with given name equals the spelled out name of the given boolean
 * value - either `"true"` or `"false"` - ignoring case. Ideally, you use this to create boolean conditions.
 */
operator fun Boolean.invoke(name: String) = property(name) ceq toString()

/**
 * Creates a [Condition] that is true if and only if given property equals the spelled out name of the given boolean
 * value - either `"true"` or `"false"` - ignoring case. Ideally, you use this to create boolean conditions.
 */
operator fun Boolean.invoke(prop: Property) = prop ceq toString()
