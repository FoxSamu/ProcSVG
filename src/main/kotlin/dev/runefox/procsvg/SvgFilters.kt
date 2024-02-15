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
 * A [NodeFilter] that only passes for `<tspan>` tags.
 */
val isTspan = tagIs("text")

/**
 * A [NodeFilter] that only passes for `<image>` tags.
 */
val isImage = tagIs("image")

/**
 * A [NodeFilter] that only passes for `<defs>` tags.
 */
val isDefs = tagIs("defs")
