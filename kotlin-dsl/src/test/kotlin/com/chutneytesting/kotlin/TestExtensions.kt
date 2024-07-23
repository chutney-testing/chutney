package com.chutneytesting.kotlin

import java.net.URL

fun String.asResourceContent(): String = this.asResource().readText()

fun String.asResource(): URL = Thread.currentThread().contextClassLoader.getResource(this)
    ?: throw RuntimeException("Resource not found [$this]")
