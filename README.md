# A Kotlin-based SVG processor (version 0.1)

This is a piece of software that can be used to transform SVG files. It uses a Kotlin DSL (domain-specific language) to define how a SVG file has to be transformed.

The tool builds on top of the W3C DOM API and Apache Batik and allows you to structurally modify SVG in many ways. Documents aren't touched any further than formatting or that what has to be processed, so when a document was exported with editor data (e.g. from Inkscape), this tool will keep this editor data in place unless you tell it explicitly to remove it.

## TODO
- Exporting PNG files
- More built in processors
- Unit testing

# Usage

## 1. Create a context
A context is created through the DSL by calling `processor { ... }`. It builds a `ProcessorDSLModel` object and returns it. This object is literally what you're calling functions on in the DSL and just stands for some model that defines how SVG files can be transformed. It's not an actual processing context yet.

You only need one `ProcessorDSLModel` instance usually, it's very reusable as long as the transformations all follow the same structure you defined.

```kotlin
val model = processor {
    // ...more to come here
}
```

### 1.1. Add node processors
Node processors are added simply by calling methods in the `processor { ... }` block. For example, `removeNode()` removes any encountered node from the document. If you need custom processing, use the `process { ... }` block.

The core idea is that any node processor applies some change to a `org.w3c.dom.Node` instance. This happens in the order they are defined in the `processor` block. The same order of processors is then also applied to its children unless `stopTraversal()` is anywhere in the `processor` block. If the node is an `Element` or `Text` node and ends up getting removed from the document tree, it will also not be traversed further.

To simply delete everything from the entire SVG document:
```kotlin
processor {
    removeNode()
}
```

### 1.2. Add conditional processors
Now a crucial thing is that whatever processor you apply will thus apply to all the traversed nodes - text nodes, comment nodes and, most importantly, element nodes. This is not always desirable though: ideally you want to filter out certain elements. This is of course also possible - using the `where` block.

The `where` block takes a parameter of type `NodeFilter` or `Condition` and then defines a block in which you can define processors in the same way you could directly inside the `processor` block. It's like an `if` statement basically, but there is an important difference. Since the DSL code is immediately evaluated, `if` statements also evaluate immediately. However, `where` defines a kind of conditional child context, which is based on the parent context in which `where` was called, and it only performs a condition check during processing time.

`where` blocks also differ from `if` statements in the way that their location does not change processor order. When a `where` condition matches on a given node, any processors in the parent block are completely ignored. Any further `where` blocks will also be ignored. Thus, `where` blocks are tested in the order they appear, but always before other processors in the block.

Also, a easy semantic of `where` blocks is that you may leave out the `where` word completely! Simply invoking a `NodeFilter` or `Condition` with a closure will have the same effect.

To change all rectangles to a red fill, unless they have the class name `dont-change` would work as follows:
```kotlin
processor {
    where(isRect) {
        // on all .dont-change elements:
        (hasClass("dont-change")) {
            // do nothing
        }
        
        // otherwise:
        setCss("fill", literally("red"))
    }
}
```

It's kinda like a big if-else block. However, sometimes it may be desirable to have common processors in several `where` blocks. You can call `inherit()` anywhere inside a `where` block to inherit all processors from the parent context into the `where` context. They are inherited at the place of the `inherit()` call so you even have control of what happens before and after. It works kinda like a `super` call!

To change all shapes to a red fill, but also give rectangles also a blue border:
```kotlin
processor {
    setCss("fill", literally("red"))
    
    isRect {
        inherit()
        setCss("stroke", literally("blue"))
    }
    isCircle {
        inherit()
    }
    isPath {
        inherit()
    }
    
    otherwise {
        // Equivalent to where(any), which matches any remaining nodes that did not get matched above
        // Note that an otherwise block simply makes sure none of the processors in the parent context are applied
        // unless inherited by any where/otherwise block
    }
}
```

### 1.3. External properties
You may have noticed the use of a function `literally`. This looks kinda weird, but it makes sense because it tells the processor that the given string stays the same in all contexts. The thing is, contexts may have different properties that can alter how the document is processed. Thus, you don't need to recreate a model for each different document you want to create, you can simply make one and reuse it with different properties!

Properties in a context are accessed through a `Property` object, which is simply an interface that looks up a property value and returns it. Property values are always strings, but the `Condition` interface provides a way to treat them as booleans.

The `literally` function is one way to obtain a `Property`, one that simply returns the given string. It's a constant string! However, there is also the `property` function, which returns a `Property` that gets an external property with a given name. More on defining external properties later.

To set all text objects with ID `replace-my-text` to contain the value of an external property named `replacement`:
```kotlin
processor {
    (isText and isId("replace-my-text")) {
        setTextContent(property("replacement"))
    }
}
```

You can also use properties in `where` blocks, but they must be turned into `Condition`s. A simple way to do that is to check if the property value equals `true`, for which a special function is defined: `true(...)`. Wait, hold on, that's a boolean? Yes, you simply invoke the boolean value `true` and give a name of a property!

To only make all rectangles red if `make-red` is `true`:
```kotlin
    (true("make-red")) {
        isRect {
            setCss("fill", "red")
        }
    }
```


### 1.4. Defining custom node processors
You can define custom node processors - they're simply defined as extension functions to `ProcessorDSLModel`. For example, the builtin `removeNode()` processor is simply defined as:
```kotlin
fun ProcessorDSLModel.removeNode() =
    process {
        it.parentNode.removeChild(it)
    }
```

## 2. Defining external properties
Now that you have set up your processing model, you can continue defining values for external properties. This is very simple! Properties are defined by implementing a `PropertyProvider`. It's a fun interface, so you can simply do `PropertyProvider { /* logic ...or magic? */ }`.

`PropertyProvider`s return strings, or null if properties are not found. `PropertyProvider.empty` simply always returns null. You can use a `Map` as source of properties by simply calling `PropertyProvider(map)`.

`PropertyProvider`s can be laid on top of each other, using an `above` chain: `x above y` (with `x` and `y` being property providers) simply means "check `x` for a property first, if it returns null check `y`". You can chain them together like this as many times as you like: `x above y above z above w above ....`.

## 3. Processing a document
Getting a `Document` is an excruciatingly tedious process in Java. You need to instantiate many different classes and hope you're doing it correct. Not with this tool! Simply call `newDocument` to get a new document with an initial `<svg>` tag, or `readDocument` to read from a `Reader` of any kind. It hides away a lot of the fuzz but it's quite limited - though most of the time you don't need such functions while using this tool.

You can process a document by simply calling `document.process(model, propertyProvider)`:
```kotlin
val model = processor {
    // ...
}

val propertyProvider = PropertyProvider(mapOf(
    // ...
))

val document = FileReader("my-fancy-svg.svg").use { readDocument(it) }
document.process(model, propertyProvider)
FileWriter("my-processed-svg.svg").use { document.write(it) }
```

If you are generating multiple documents from one template, be aware that processing a document modifies the document instance itself. Make a copy using the `document.copy()` function first.

# License (GPL v3)
    Copyright (C) 2024 Samū

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
