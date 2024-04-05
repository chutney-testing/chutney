/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
