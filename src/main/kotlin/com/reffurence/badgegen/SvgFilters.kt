package com.reffurence.badgegen

/**
 * A [NodeFilter] that only passes for `<svg>` tags.
 */
val isSvg = tagIs("svg")

/**
 * A [NodeFilter] that only passes for `<g>` tags.
 */
val isG = tagIs("g")

/**
 * A [NodeFilter] that only passes for `<rect>` tags.
 */
val isRect = tagIs("rect")

/**
 * A [NodeFilter] that only passes for `<circle>` tags.
 */
val isCircle = tagIs("circle")

/**
 * A [NodeFilter] that only passes for `<path>` tags.
 */
val isPath = tagIs("path")

/**
 * A [NodeFilter] that only passes for `<text>` tags.
 */
val isText = tagIs("text")

/**
 * A [NodeFilter] that only passes for `<image>` tags.
 */
val isImage = tagIs("image")

/**
 * A [NodeFilter] that only passes for `<defs>` tags.
 */
val isDefs = tagIs("defs")
