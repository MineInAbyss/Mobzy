package com.mieninabyss.mobzy.processor

import java.io.File
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror


//this is the simplest way of getting the classes before they finish being created
fun getTypeMirror(block: () -> Unit): TypeMirror? = try {
    block()
    null
} catch (e: MirroredTypeException) {
    e.typeMirror
}

fun getTypeMirrors(block: () -> Unit): MutableList<out TypeMirror>? = try {
    block()
    null
} catch (e: MirroredTypesException) {
    e.typeMirrors
}

fun TypeElement.readSource(): List<String> {
    val path = qualifiedName.toString().replace('.', '/')
    val ext = ".kt"
    return File("src/main/java/$path$ext").readLines()
}