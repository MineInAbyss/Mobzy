package com.mieninabyss.mobzy.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import me.eugeniomarletti.kotlin.metadata.jvm.internalName
import net.minecraft.server.v1_16_R1.EntityTypes
import net.minecraft.server.v1_16_R1.World
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

annotation class CustomMobOverrides(val createFor: Array<KClass<*>>)

@Suppress("unused")
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(SharedOverridesProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@ExperimentalStdlibApi
@KotlinPoetMetadataPreview
class SharedOverridesProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(CustomMobOverrides::class.java.name)
    }

    private val elements: Elements by lazy { processingEnv.elementUtils }
    private val types: Types by lazy { processingEnv.typeUtils }

    val classInspector by lazy { ElementsClassInspector.create(elements, types) }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(CustomMobOverrides::class.java)
                .map { it as TypeElement }
                .associateWith { it.toTypeSpec(classInspector) }
                .forEach { (element, typeSpec) ->
                    if (!createType(element, typeSpec)) return true
                }
        return true
    }

    //TODO name things better
    private fun createType(element: TypeElement, overridesClassSpec: TypeSpec): Boolean {
        val overrides = element.getAnnotation(CustomMobOverrides::class.java)

        getTypeMirrors { overrides.createFor }?.forEach { createFor ->
            val packageName = element.packageName
            val createForElement = (createFor as DeclaredType).asElement()
            val fileName = "Mobzy${createForElement.simpleName}"
            val fileBuilder = FileSpec.builder(packageName, fileName)

            val lines = element.readSource()

            val newClass = TypeSpec.classBuilder(fileName)
                    .superclass(createFor.asTypeName())
                    .primaryConstructor(FunSpec.constructorBuilder()
                            .addParameter("world", World::class)
                            .addParameter("type", typeOf<EntityTypes<*>>().asTypeName())
                            .build())
                    .addSuperclassConstructorParameter("type as EntityTypes<$createFor>, world".replace(' ', '·'))
                    .addSuperinterfaces(overridesClassSpec.superinterfaces.map { it.key })
                    .addModifiers(KModifier.ABSTRACT)
            fileBuilder.addType(newClass.build())

            val file = fileBuilder.build().toJavaFileObject()
            val outputLines = file.openReader(true).readLines().toMutableList()

            val existingImports = outputLines.filter { it.startsWith("import") }
            outputLines.addAll(1, lines.filter { it.startsWith("import") && !existingImports.contains(it) })

            val start = lines.indexOfFirst { it.contains("class ${element.simpleName}") }
            val innerCode = lines.drop(start + 1)
            val classLines = innerCode.takeWhile { line ->
                !line.startsWith('}')
            }
            outputLines[outputLines.lastIndex] = outputLines[outputLines.lastIndex] + " {"
            outputLines.addAll(classLines + "}")

            val generatedDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
            val writeTo = File(generatedDirectory, file.toUri().toString())
            writeTo.parentFile.mkdirs()
            writeTo.writeText(outputLines.joinToString(separator = "\n"))
        }
        return true
    }

    private val TypeElement.packageName get() = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()
}

fun getTypeMirrors(block: () -> Unit): MutableList<out TypeMirror>? {
    //this is the simplest way of getting the classes before they finish being created
    try {
        block()
    } catch (e: MirroredTypesException) {
        return e.typeMirrors
    }
    return null
}

fun TypeElement.readSource(): List<String> {
    val path = internalName.replace(',', '/')
    val ext = ".kt"
    return File("src/main/java/$path$ext").readLines()
}