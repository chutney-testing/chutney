package com.chutneytesting.idea.runner.settings.ui

import com.google.common.collect.ImmutableMap
import com.intellij.util.containers.ContainerUtil
import org.jdom.Element

/**
 * Holds environment variables configuration:
 *
 *  * list of user-defined environment variables
 *  * boolean flag - whether to pass system environment
 *
 * Instances of this class are immutable objects, so it can be safely passed across threads.
 */
class ChutneyVariablesData private constructor(envs: Map<String, String>) {
    private val myEnvs: ImmutableMap<String, String>
    /**
     * @return immutable Map instance containing user-defined environment variables (iteration order is reliable user-specified)
     */
    val envs: MutableMap<String, String>
        get() = myEnvs

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val data = o as ChutneyVariablesData
        return myEnvs == data.myEnvs
    }

    override fun hashCode(): Int {
        var result = myEnvs.hashCode()
        result = 31 * result
        return result
    }

    override fun toString(): String {
        return "envs=$myEnvs"
    }

    fun writeExternal(parent: Element) {
        val envsElement = Element(ENVS)
        for ((key, value) in myEnvs) {
            envsElement.addContent(Element(ENV).setAttribute(NAME, key).setAttribute(VALUE, value))
        }
        parent.addContent(envsElement)
    }

    companion object {
        @JvmField
        val DEFAULT = ChutneyVariablesData(ImmutableMap.of())
        private const val ENVS = "envs"
        const val ENV = "env"
        private const val NAME = "name"
        private const val VALUE = "value"
        fun readExternal(element: Element): ChutneyVariablesData {
            val envsElement = element.getChild(ENVS) ?: return DEFAULT
            var envs: MutableMap<String, String> = ImmutableMap.of()
            for (envElement in envsElement.getChildren(ENV)) {
                val envName = envElement.getAttributeValue(NAME)
                val envValue = envElement.getAttributeValue(VALUE)
                if (envName != null && envValue != null) {
                    if (envs.isEmpty()) {
                        envs = ContainerUtil.newLinkedHashMap()
                    }
                    envs[envName] = envValue
                }
            }
            return create(envs)
        }

        /**
         * @param envs Map instance containing user-defined environment variables
         * (iteration order should be reliable user-specified, like [LinkedHashMap] or [ImmutableMap])
         */
        @JvmStatic
        fun create(envs: Map<String, String>): ChutneyVariablesData {
            return if (envs.isEmpty()) DEFAULT else ChutneyVariablesData(envs)
        }
    }

    init {
        myEnvs = ImmutableMap.copyOf(envs)
    }
}
