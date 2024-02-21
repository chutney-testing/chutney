package com.chutneytesting.idea.completion.contributor

import java.util.*

class ReferencePrefixExtractor {

    fun getPrefix(offset: Int, text: String): Optional<String> {
        var offset = offset
        val prefixBuilder = StringBuilder()
        while (offset > 0 && (Character.isLetterOrDigit(text[offset])
                    || text[offset] == '#' || text[offset] == '/')
        ) {
            prefixBuilder.insert(0, text[offset])
            offset--
        }
        return if (prefixBuilder.isNotEmpty()) Optional.of(prefixBuilder.toString()) else Optional.empty()
    }
}
