package com.mieninabyss.mobzy.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.classinspector.elements.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.toTypeSpec
import net.minecraft.server.v1_16_R2.EntityTypes
import net.minecraft.server.v1_16_R2.World
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@Retention(AnnotationRetention.SOURCE)
annotation class GenerateFromBase(val base: KClass<*>, val createFor: Array<KClass<*>>)

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

    override fun getSupportedAnnotationTypes() = mutableSetOf(GenerateFromBase::class.java.name)

    private val elements: Elements by lazy { processingEnv.elementUtils }
    private val types: Types by lazy { processingEnv.typeUtils }
    private val classInspector by lazy { ElementsClassInspector.create(elements, types) }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateFromBase::class.java)
                .filterIsInstance<TypeElement>()
                .forEach { element ->
                    val annotation = element.getAnnotation(GenerateFromBase::class.java)
                    val base = (getTypeMirror { annotation.base } as DeclaredType).asElement() as TypeElement
                    val targets = getTypeMirrors { annotation.createFor }

                    targets?.forEach { target ->
                        createTargetsFromBase(base, target)
                    }
                }
        return true
    }

    private fun createTargetsFromBase(baseElement: TypeElement, targetMirror: TypeMirror) {
        val baseSpec = baseElement.toTypeSpec(classInspector)
        val targetElement = (targetMirror as DeclaredType).asElement()

        val packageName = baseElement.packageName
        val fileName = "Mobzy${targetElement.simpleName}"
        val fileBuilder = FileSpec.builder(packageName, fileName)

        val generatedDirectory = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
        val sourceLines = baseElement.readSource(File(generatedDirectory))

        //create class
        val newClass = TypeSpec.classBuilder(fileName)
                .superclass(targetMirror.asTypeName())
                .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter("world", World::class)
                        .addParameter("type", typeOf<EntityTypes<*>>().asTypeName())
                        .build())
                .addSuperclassConstructorParameter("type as EntityTypes<$targetMirror>, world".replace(' ', '·')) //https://github.com/square/kotlinpoet#spaces-wrap-by-default
                .addSuperinterfaces(baseSpec.superinterfaces.map { it.key })
                .addModifiers(KModifier.ABSTRACT)

        fileBuilder.addType(newClass.build())

        //build to file
        val file = fileBuilder.build().toJavaFileObject()
        val outputLines = file.openReader(true).readLines().toMutableList()

        //add imports
        val existingImports = outputLines.filter { it.startsWith("import") }
        outputLines.addAll(1, sourceLines.filter { it.startsWith("import") && !existingImports.contains(it) })

        //copy code within the base class
        val start = sourceLines.indexOfFirst { it.contains("class ${baseElement.simpleName}") }
        val innerCode = sourceLines.drop(start + 1)
        val classLines = innerCode.takeWhile { line -> !line.startsWith('}') }

        outputLines[outputLines.lastIndex] = outputLines[outputLines.lastIndex] + " {"
        outputLines.addAll(classLines + "}")

        //write file
        val writeTo = File(generatedDirectory, file.toUri().toString())
        writeTo.parentFile.mkdirs()
        writeTo.writeText(outputLines.joinToString(separator = "\n"))
    }

    private val TypeElement.packageName get() = processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()
}
