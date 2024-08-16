/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.util

import io.github.classgraph.ClassGraph
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

object ClassGraphUtil {
    fun findAllAnnotatedFunctions(
        packageName: String,
        annotationClass: KClass<*>
    ): List<KFunction<*>> {
        val annotationName = annotationClass.java.canonicalName
        return ClassGraph()
            .enableAllInfo()
            .acceptPackages(packageName)
            .scan().use { scanResult ->
                scanResult.getClassesWithMethodAnnotation(annotationName).flatMap { routeClassInfo ->
                    routeClassInfo.methodInfo.filter { function ->
                        function.hasAnnotation(annotationName)
                    }.mapNotNull { method ->
                        method.loadClassAndGetMethod().kotlinFunction
                    }
                }
            }
    }
}
