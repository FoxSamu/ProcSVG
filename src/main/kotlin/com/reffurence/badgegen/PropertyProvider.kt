package com.reffurence.badgegen

/**
 * A provider of properties. It functions more or less the same as a [`Map<String, String>`](Map) but it only has [get].
 */
fun interface PropertyProvider {
    /**
     * Gets a property from the provider. If the property is defined, it returns its string value. If it's not defined,
     * it returns null.
     */
    operator fun get(name: String): String?

    companion object {
        /**
         * A [PropertyProvider] that knows no properties. It always returns null.
         */
        val empty = PropertyProvider { null }

        /**
         * A [PropertyProvider] that reflects the properties from a [Map]. When the [Map] is updated, this is reflected
         * in the provider.
         */
        operator fun invoke(map: Map<String, String>) = PropertyProvider { map[it] }

        /**
         * A [PropertyProvider] that reflects the properties from a list of key-value pairs.
         */
        operator fun invoke(vararg pairs: Pair<String, String>) = PropertyProvider(mapOf(*pairs))
    }
}

/**
 * Puts the left [PropertyProvider] above the right [PropertyProvider]. This creates a kind of stack of providers, and
 * it looks down the stack to resolve a property from it. If the upper provider defines a property, this overrides the
 * property in the lower. If the upper does not define a property, it falls back on the lower provider. Only if neither
 * provide a property, does it return null.
 */
infix fun PropertyProvider.above(other: PropertyProvider): PropertyProvider {
    return PropertyProvider {
        this[it] ?: other[it]
    }
}
