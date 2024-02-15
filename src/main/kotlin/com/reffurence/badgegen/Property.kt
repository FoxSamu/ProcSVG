package com.reffurence.badgegen

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
infix fun Property.eq(value: Property) = Condition { apply(it) == value.apply(it) }

/**
 * A [Condition] that checks if the left property value equals a given string.
 */
infix fun Property.eq(value: String) = Condition { apply(it) == value }

/**
 * A [Condition] that checks if the left property value does not equal that of the right property.
 */
infix fun Property.neq(value: Property) = Condition { apply(it) != value.apply(it) }

/**
 * A [Condition] that checks if the left property value does not equal a given string.
 */
infix fun Property.neq(value: String) = Condition { apply(it) != value }

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
