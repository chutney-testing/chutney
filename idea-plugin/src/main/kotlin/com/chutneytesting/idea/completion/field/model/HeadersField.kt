package com.chutneytesting.idea.completion.field.model

import com.google.common.collect.ImmutableList

internal class HeadersField(name: String?) : ObjectField(name) {

    override val children: List<Field>
        get() = FIELDS

    companion object {
        private val FIELDS: List<Field> = ImmutableList.of(StringField("description"), StringField("type"))
    }
}
