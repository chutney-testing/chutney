/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.dsl

data class ChutneyEnvironment(
    val name: String,
    val description: String = "",
    val targets: List<ChutneyTarget> = emptyList(),
    val variables: Map<String, String> = emptyMap()
) {

    init {
        val notUniqueTargets = targets
            .groupBy { it.name }
            .filterValues { it.size > 1 }
            .keys
        if (notUniqueTargets.isNotEmpty()) {
            throw IllegalArgumentException("Targets are not unique : " + notUniqueTargets.joinToString(", "))
        }
    }

    fun findTarget(targetName: String?): ChutneyTarget? {
        return try {
            targets.first { it.name == targetName }
        } catch (nsee: NoSuchElementException) {
            null
        }
    }

    override fun toString(): String {
        return "ChutneyEnvironment(name='$name', description='$description')"
    }
}

data class ChutneyTarget(
    val name: String,
    val url: String,
    val properties: Map<String, String> = emptyMap()
)

@DslMarker
annotation class ChutneyEnvironmentDsl

fun Environment(
    name: String,
    description: String = name,
    block: ChutneyEnvironmentBuilder.() -> Unit
): ChutneyEnvironment {
    return ChutneyEnvironmentBuilder(name, description).apply(block).build()
}

@ChutneyEnvironmentDsl
class ChutneyEnvironmentBuilder(val name: String, val description: String) {
    private val targets = mutableListOf<ChutneyTarget>()

    fun Target(block: ChutneyTargetBuilder.() -> Unit) {
        targets.add(ChutneyTargetBuilder().apply(block).build())
    }

    fun build(): ChutneyEnvironment = ChutneyEnvironment(name, description, targets)

}


@ChutneyEnvironmentDsl
class ChutneyTargetBuilder {
    private var name: String = ""
    private var url: String = ""
    private val properties = mutableListOf<Pair<String, String>>()

    fun Name(name: String) {
        this.name = name
    }

    fun Url(url: String) {
        this.url = url
    }

    fun Properties(vararg properties: Pair<String, String>) {
        this.properties.addAll(properties)
    }

    fun build(): ChutneyTarget = ChutneyTarget(name, url, properties.toMap())

}
