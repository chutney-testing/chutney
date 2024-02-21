package com.chutneytesting.idea.util

object EnumUtils {
    fun <E : Enum<E>?> findEnum(enumClass: Class<E>, name: String): E? {
        return findEnum(enumClass, name, true)
    }

    fun <E : Enum<E>?> findEnum(enumClass: Class<E>, name: String, caseSensitive: Boolean): E? {
        val enumConstants = enumClass.enumConstants
        for (e in enumConstants) {
            if (if (caseSensitive) e!!.name == name else e!!.name.equals(name, ignoreCase = true)) {
                return e
            }
        }
        return null
    }
}
