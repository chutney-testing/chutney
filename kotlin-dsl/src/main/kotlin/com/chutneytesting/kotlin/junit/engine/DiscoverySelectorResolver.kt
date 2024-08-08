package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyStep
import com.chutneytesting.kotlin.junit.api.ChutneyTest
import org.junit.platform.commons.support.AnnotationSupport
import org.junit.platform.commons.support.HierarchyTraversalMode
import org.junit.platform.commons.support.ReflectionSupport
import org.junit.platform.engine.*
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathResourceSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.launcher.LauncherDiscoveryRequest
import java.io.File
import java.lang.reflect.Method
import java.util.function.Predicate

fun UniqueId.addClass(className: String): UniqueId {
    return this.append("class", className)
}

fun UniqueId.addScenario(methodName: String, index: Int): UniqueId {
    return this.append("scenario", "$methodName - $index")
}

fun UniqueId.addStep(stepIndex: Int): UniqueId {
    return this.append("step", "$stepIndex")
}

class DiscoverySelectorResolver(private val stepAsTest: Boolean = true) {

    private val classNotNullCanonicalNamePredicate = Predicate { clazz: Class<*> -> clazz.canonicalName != null }

    fun resolveSelectors(discoveryRequest: EngineDiscoveryRequest, engineDescriptor: ChutneyEngineDescriptor): TestDescriptor {

        val classSelectors = discoveryRequest.getSelectorsByType(ClassSelector::class.java)
        val classSelectorPredicate = selectorToPredicate(classSelectors) {
            buildNamePredicate(it.className)
        }

        val classPathRootSelectors = discoveryRequest.getSelectorsByType(ClasspathRootSelector::class.java)
        val classPathRootPredicate = selectorToPredicate(classPathRootSelectors) {
            buildClassPathRootClassPredicate(it.classpathRoot.path)
        }

        val classPathResourceSelectors = discoveryRequest.getSelectorsByType(ClasspathResourceSelector::class.java)
        val classPathResourcePredicate = selectorToPredicate(classPathResourceSelectors) {
            buildClassPathResourceClassPredicate(it.classpathResourceName)
        }

        val methodSelectors = discoveryRequest.getSelectorsByType(MethodSelector::class.java)
        val methodPredicate = selectorToPredicate(methodSelectors) {
            buildNamePredicate(it.methodName)
        }
        val classFromMethodPredicate = selectorToPredicate(methodSelectors) {
            buildNamePredicate(it.className)
        }

        ReflectionSupport
            .findAllClassesInPackage(
                "",
                classNotNullCanonicalNamePredicate
                    .and(classPathRootPredicate)
                    .and(classPathResourcePredicate),
                classSelectorPredicate
                    .and(classFromMethodPredicate)
            )
            .flatMap {
                AnnotationSupport.findAnnotatedMethods(
                    it,
                    ChutneyTest::class.java,
                    HierarchyTraversalMode.TOP_DOWN
                )
            }
            .filter {
                it.returnType == ChutneyScenario::class.java || it.returnType == List::class.java
            }
            .filter {
                methodPredicate.test(it.name)
            }
            .map {
                mapMethodToClassDescriptor(it, engineDescriptor)
            }
            .distinct()
            .forEach { classDescriptor ->
                classDescriptor.children
                    .filter { !postDiscoveryFilter(it as ChutneyScenarioDescriptor, discoveryRequest) }
                    .forEach { classDescriptor.removeChild(it) }
            }

        return engineDescriptor
    }

    /**
     * Gradle don't do it, so we do it, although it's documented to not do it
     */
    private fun postDiscoveryFilter(
        testDescriptor: ChutneyScenarioDescriptor,
        discoveryRequest: EngineDiscoveryRequest
    ): Boolean {
        if (discoveryRequest is LauncherDiscoveryRequest) {
            val postDiscoveryPredicate = Filter.composeFilters(discoveryRequest.postDiscoveryFilters).toPredicate()
            return postDiscoveryPredicate.test(testDescriptor)
        }
        return true
    }

    private fun mapMethodToClassDescriptor(method: Method, engineDescriptor: TestDescriptor): ChutneyClassDescriptor {
        val environmentName = method.getAnnotation(ChutneyTest::class.java).environment
        val classInstance = method.declaringClass.getConstructor().newInstance()
        val environment = resolveEnvironment(classInstance, environmentName)
        val chutneyClassDescriptor = resolveChutneyClassDescriptor(engineDescriptor, method)

        when (val methodResult = method.invoke(classInstance)) {
            is ChutneyScenario -> {
                chutneyClassDescriptor
                    .addChild(
                        buildChutneyScenarioDescriptor(chutneyClassDescriptor, method, methodResult, environmentName, environment)
                    )
            }
            is List<*> -> {
                methodResult
                    .filterIsInstance<ChutneyScenario>()
                    .forEachIndexed { index, it ->
                        chutneyClassDescriptor
                            .addChild(
                                buildChutneyScenarioDescriptor(chutneyClassDescriptor, method, it, environmentName, environment, index)
                            )
                    }
            }
        }

        return chutneyClassDescriptor
    }

    private fun resolveEnvironment(it: Any, environmentName: String): ChutneyEnvironment? {
        return try {
            it.javaClass.getDeclaredField(environmentName).get(null) as? ChutneyEnvironment
        } catch (e: Exception) {
            null
        }
    }

    private fun resolveChutneyClassDescriptor(
        parentTestDescriptor: TestDescriptor,
        method: Method
    ): ChutneyClassDescriptor {
        val classSource = ClassSource.from(method.declaringClass)
        val classDescriptorUniqueId = parentTestDescriptor.uniqueId.addClass(classSource.className)

        val findByUniqueId = parentTestDescriptor.findByUniqueId(classDescriptorUniqueId)

        return if (findByUniqueId.isPresent) {
            findByUniqueId.get() as ChutneyClassDescriptor
        } else {
            val chutneyClassDescriptor = ChutneyClassDescriptor(
                classDescriptorUniqueId,
                classSource.javaClass.simpleName,
                classSource
            )
            parentTestDescriptor.addChild(chutneyClassDescriptor)
            chutneyClassDescriptor
        }
    }

    private fun buildChutneyScenarioDescriptor(
        parentTestDescriptor: TestDescriptor,
        method: Method,
        chutneyScenario: ChutneyScenario,
        environmentName: String,
        environment: ChutneyEnvironment?,
        scenarioIndex: Int = 1
    ): ChutneyScenarioDescriptor {
        val methodSource = MethodSource.from(method)

        val chutneyScenarioDescriptor = ChutneyScenarioDescriptor(
            parentTestDescriptor.uniqueId.addScenario(method.name, scenarioIndex),
            "${method.name} - ${chutneyScenario.title}",
            methodSource,
            chutneyScenario,
            environmentName,
            environment,
            stepAsTest
        )

        if (stepAsTest) {
            listOfNotNull(
                chutneyScenario.givens,
                chutneyScenario.`when`?.let { listOf(it) } ?: emptyList(),
                chutneyScenario.thens
            ).flatten().forEachIndexed { index, chutneyStep ->
                chutneyScenarioDescriptor.addChild(
                    buildChutneyStepDescriptor(chutneyScenarioDescriptor, index, chutneyStep, methodSource)
                )
            }
        }

        return chutneyScenarioDescriptor
    }

    private fun buildChutneyStepDescriptor(
        parentTestDescriptor: TestDescriptor,
        stepIndex: Int,
        chutneyStep: ChutneyStep,
        scenarioMethodSource: MethodSource
    ): ChutneyStepDescriptor {
        val chutneyStepDescriptor = ChutneyStepDescriptor(
            parentTestDescriptor.uniqueId.addStep(stepIndex),
            chutneyStep.description,
            scenarioMethodSource,
            chutneyStep,
        )

        chutneyStep.subSteps.forEachIndexed { index, subStep ->
            chutneyStepDescriptor.addChild(
                buildChutneyStepDescriptor(chutneyStepDescriptor, index, subStep, scenarioMethodSource)
            )
        }

        return chutneyStepDescriptor
    }

    private fun buildNamePredicate(name: String): Predicate<String> {
        return Predicate { aName: String ->
            aName == name
        }
    }

    private fun buildClassPathRootClassPredicate(classPathRoot: String): Predicate<Class<*>> {
        val normalizePath = normalizePath(classPathRoot)
        return Predicate {
            classPackageToPath(it).startsWith(normalizePath)
        }
    }

    private fun buildClassPathResourceClassPredicate(classPathResource: String): Predicate<Class<*>> {
        val path = javaClass.classLoader.getResource(classPathResource)?.path ?: ""
        return Predicate {
            classToPath(it) == path
        }
    }

    private fun classPackageToPath(clazz: Class<*>): String {
        return normalizePath(javaClass.classLoader.getResource(clazz.packageName.replace(".", "/"))!!.path)
    }

    private fun classToPath(clazz: Class<*>): String {
        return javaClass.classLoader.getResource(clazz.canonicalName.replace(".", "/").plus(".class"))!!.path
    }

    private fun normalizePath(path: String): String {
        return File(path).normalize().path
    }

    private fun <T : DiscoverySelector, R> selectorToPredicate(
        discoverySelectors: List<T>,
        transform: (T) -> Predicate<R>
    ): Predicate<R> {
        return discoverySelectors
            .map(transform)
            .reduceOrNull { p1, p2 ->
                p1.or(p2)
            } ?: Predicate { true }
    }

}
