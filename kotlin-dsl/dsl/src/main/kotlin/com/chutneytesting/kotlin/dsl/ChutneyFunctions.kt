package com.chutneytesting.kotlin.dsl

@Deprecated("Use jsonPath global function")
fun json(variable: String, path: String = JSON_PATH_ROOT): String {
    require(variable.isNotBlank()) { "variable cannot be empty" }
    return "json(${variable.spELVar}, ${path.elString()})".spEL()
}

private fun chutneyFunction(
    name: String,
    elEval: Boolean = true,
    vararg expressionParameters: String
): String {
    val expr = name.spELVar
        .plus(expressionParameters.joinToString(prefix = "(", postfix = ")"))

    if (elEval) {
        return expr.elEval()
    }
    return expr
}

// Based on chutney.functions file

fun jsonPath(jsonELExpr: String, path: String = JSON_PATH_ROOT.elString(), elEval: Boolean = true): String {
    require(jsonELExpr.isNotBlank()) { "jsonELExpr cannot be empty" }
    return chutneyFunction("jsonPath", elEval, jsonELExpr, path.elString())
}

fun jsonSerialize(objectELExpr: String, elEval: Boolean = true): String {
    require(objectELExpr.isNotBlank()) { "objectELExpr cannot be empty" }
    return chutneyFunction("jsonSerialize", elEval, objectELExpr)
}

fun jsonMerge(objectELExprA: String, objectELExprB: String, elEval: Boolean = true): String {
    require(objectELExprA.isNotBlank()) { "objectELExprA cannot be empty" }
    require(objectELExprB.isNotBlank()) { "objectELExprB cannot be empty" }
    return chutneyFunction("jsonMerge", elEval, objectELExprA, objectELExprB)
}

fun jsonSetMany(objectELExpr: String, map: String, elEval: Boolean = true): String {
    require(objectELExpr.isNotBlank()) { "objectELExpr cannot be empty" }
    require(map.isNotBlank()) { "map cannot be empty" }
    return chutneyFunction("jsonSetMany", elEval, objectELExpr, map)
}

fun jsonSet(objectELExpr: String, path: String, value: String, elEval: Boolean = true): String {
    require(objectELExpr.isNotBlank()) { "objectELExpr cannot be empty" }
    require(path.isNotBlank()) { "path cannot be empty" }
    require(value.isNotBlank()) { "value cannot be empty" }
    return chutneyFunction("jsonSet", elEval, objectELExpr, path, value)
}

private val XPATH_ROOT = "//*".elString()
fun xpath(xmlELExpr: String, path: String = XPATH_ROOT, elEval: Boolean = true): String {
    require(xmlELExpr.isNotBlank()) { "xmlELExpr cannot be empty" }
    return chutneyFunction("xpath", elEval, xmlELExpr, path.elString())
}

fun xpathNs(
    xmlELExpr: String,
    path: String = XPATH_ROOT,
    prefixes: Map<String, String>,
    elEval: Boolean = true
): String {
    require(xmlELExpr.isNotBlank()) { "xmlELExpr cannot be empty" }
    require(prefixes.isNotEmpty()) { "prefixes cannot be empty" }
    return chutneyFunction("xpathNs", elEval, xmlELExpr, path.elString(), prefixes.elMap())
}


fun getSoapBody(login: String, password: String, bodyELExpr: String, elEval: Boolean = true): String {
    require(login.isNotBlank()) { "login cannot be empty" }
    require(password.isNotBlank()) { "password cannot be empty" }
    require(bodyELExpr.isNotBlank()) { "bodyELExpr cannot be empty" }
    return chutneyFunction("getSoapBody", elEval, login.elString(), password.elString(), bodyELExpr)
}


fun date(date: String, format: String? = null, elEval: Boolean = true): String {
    require(date.isNotBlank()) { "date cannot be empty" }
    return chutneyFunction("date", elEval, date.elString(), format?.elString() ?: "null")
}

fun currentTimeMillis(elEval: Boolean = true): String {
    return chutneyFunction("currentTimeMillis", elEval)
}

fun now(elEval: Boolean = true): String {
    return chutneyFunction("now", elEval)
}

fun dateFormatter(pattern: String, elEval: Boolean = true): String {
    require(pattern.isNotBlank()) { "pattern cannot be empty" }
    return chutneyFunction("dateFormatter", elEval, pattern.elString())
}

fun dateFormatterWithLocale(pattern: String, locale: String, elEval: Boolean = true): String {
    require(pattern.isNotBlank()) { "pattern cannot be empty" }
    return chutneyFunction("dateFormatterWithLocale", elEval, pattern.elString(), locale.elString())
}

fun isoDateFormatter(type: String, elEval: Boolean = true): String {
    require(type.isNotBlank()) { "type cannot be empty" }
    return chutneyFunction("isoDateFormatter", elEval, type.elString())
}

fun timeAmount(text: String, elEval: Boolean = true): String {
    require(text.isNotBlank()) { "text cannot be empty" }
    return chutneyFunction("timeAmount", elEval, text.elString())
}

fun timeUnit(unit: String, elEval: Boolean = true): String {
    require(unit.isNotBlank()) { "unit cannot be empty" }
    return chutneyFunction("timeUnit", elEval, unit.elString())
}

fun str_replace(inputELExpr: String, regExp: String, replacement: String = "", elEval: Boolean = true): String {
    require(inputELExpr.isNotBlank()) { "inputELExpr cannot be empty" }
    require(regExp.isNotBlank()) { "regExp cannot be empty" }
    return chutneyFunction("str_replace", elEval, inputELExpr, regExp.elString(), replacement.elString())
}


fun generate(elEval: Boolean = true): String {
    return chutneyFunction("generate", elEval)
}

fun generate_uuid(elEval: Boolean = true): String {
    return chutneyFunction("generate().uuid", elEval)
}

fun generate_randomLong(elEval: Boolean = true): String {
    return chutneyFunction("generate().randomLong", elEval)
}

fun generate_randomInt(max: Int, elEval: Boolean = true): String {
    require(max > 0) { "max must be positive" }
    return chutneyFunction("generate().randomInt", elEval, "$max")
}

fun generate_id(prefix: String = "", length: Int, elEval: Boolean = true): String {
    return chutneyFunction("generate().id", elEval, prefix.elString(), "$length")
}

fun generate_id(length: Int, suffix: String = "", elEval: Boolean = true): String {
    return chutneyFunction("generate().id", elEval, "$length", suffix.elString())
}

fun generate_id(prefix: String = "", length: Int, suffix: String = "", elEval: Boolean = true): String {
    return chutneyFunction("generate().id", elEval, prefix.elString(), "$length", suffix.elString())
}


fun wiremock_extractHeadersAsMap(loggedRequestELVariable: String, elEval: Boolean = true): String {
    require(loggedRequestELVariable.isNotBlank()) { "loggedRequestELVariable cannot be empty" }
    return chutneyFunction("extractHeadersAsMap", elEval, loggedRequestELVariable.spELVar)
}

fun wiremock_extractParameters(loggedRequestELVariable: String, elEval: Boolean = true): String {
    require(loggedRequestELVariable.isNotBlank()) { "loggedRequestELVariable cannot be empty" }
    return chutneyFunction("extractParameters", elEval, loggedRequestELVariable.spELVar)
}


fun nullable(elVariable: String, elEval: Boolean = true): String {
    require(elVariable.isNotBlank()) { "elVariable cannot be empty" }
    return chutneyFunction("nullable", elEval, elVariable.spELVar)
}


fun micrometerRegistry(registryClassName: String, elEval: Boolean = true): String {
    require(registryClassName.isNotBlank()) { "registryClassName cannot be empty" }
    return chutneyFunction("micrometerRegistry", elEval, registryClassName.elString())
}


fun tcpPort(elEval: Boolean = true): String {
    return chutneyFunction("tcpPort", elEval)
}

fun tcpPorts(nb: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    return chutneyFunction("tcpPorts", elEval, "$nb")
}

fun tcpPortMin(min: Int, elEval: Boolean = true): String {
    require(min > 0) { "min must be positive" }
    return chutneyFunction("tcpPortMin", elEval, "$min")
}

fun tcpPortMinMax(min: Int, max: Int, elEval: Boolean = true): String {
    require(min > 0) { "min must be positive" }
    require(max > 0) { "max must be positive" }
    require(max > min) { "max must be greater than min" }
    return chutneyFunction("tcpPortMinMax", elEval, "$min", "$max")
}

fun tcpPortsMinMax(nb: Int, min: Int, max: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    require(min > 0) { "min must be positive" }
    require(max > 0) { "max must be positive" }
    require(max > min) { "max must be greater than min" }
    return chutneyFunction("tcpPortsMinMax", elEval, "$nb", "$min", "$max")
}

fun tcpPortRandomRange(range: Int, elEval: Boolean = true): String {
    require(range > 0) { "range must be positive" }
    return chutneyFunction("tcpPortRandomRange", elEval, "$range")
}

fun tcpPortsRandomRange(nb: Int, range: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    require(range > 0) { "range must be positive" }
    return chutneyFunction("tcpPortsRandomRange", elEval, "$nb", "$range")
}

fun udpPort(elEval: Boolean = true): String {
    return chutneyFunction("udpPort", elEval)
}

fun udpPorts(nb: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    return chutneyFunction("udpPorts", elEval, "$nb")
}

fun udpPortMin(min: Int, elEval: Boolean = true): String {
    require(min > 0) { "min must be positive" }
    return chutneyFunction("udpPortMin", elEval, "$min")
}

fun udpPortMinMax(min: Int, max: Int, elEval: Boolean = true): String {
    require(min > 0) { "min must be positive" }
    require(max > 0) { "max must be positive" }
    require(max > min) { "max must be greater than min" }
    return chutneyFunction("udpPortMinMax", elEval, "$min", "$max")
}

fun udpPortsMinMax(nb: Int, min: Int, max: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    require(min > 0) { "min must be positive" }
    require(max > 0) { "max must be positive" }
    require(max > min) { "max must be greater than min" }
    return chutneyFunction("udpPortsMinMax", elEval, "$nb", "$min", "$max")
}

fun udpPortRandomRange(range: Int, elEval: Boolean = true): String {
    require(range > 0) { "range must be positive" }
    return chutneyFunction("udpPortRandomRange", elEval, "$range")
}

fun udpPortsRandomRange(nb: Int, range: Int, elEval: Boolean = true): String {
    require(nb > 0) { "nb must be positive" }
    require(range > 0) { "range must be positive" }
    return chutneyFunction("udpPortsRandomRange", elEval, "$nb", "$range")
}


fun resourcePath(path: String, elEval: Boolean = true): String {
    require(path.isNotBlank()) { "path cannot be empty" }
    return chutneyFunction("resourcePath", elEval, path.elString())
}

fun resourcesPath(path: String, elEval: Boolean = true): String {
    require(path.isNotBlank()) { "path cannot be empty" }
    return chutneyFunction("resourcesPath", elEval, path.elString())
}

fun resourceContent(path: String, charset: String? = null, elEval: Boolean = true): String {
    require(path.isNotBlank()) { "path cannot be empty" }
    require(charset == null || charset.isNotBlank()) { "charset cannot be empty" }
    return chutneyFunction("resourceContent", elEval, path.elString(), charset?.elString() ?: "null")
}


fun escapeJson(textELExpr: String, elEval: Boolean = true): String {
    require(textELExpr.isNotBlank()) { "textELExpr cannot be empty" }
    return chutneyFunction("escapeJson", elEval, textELExpr)
}

fun unescapeJson(textELExpr: String, elEval: Boolean = true): String {
    require(textELExpr.isNotBlank()) { "textELExpr cannot be empty" }
    return chutneyFunction("unescapeJson", elEval, textELExpr)
}

fun escapeXml10(xmlELExpr: String, elEval: Boolean = true): String {
    require(xmlELExpr.isNotBlank()) { "xmlELExpr cannot be empty" }
    return chutneyFunction("escapeXml10", elEval, xmlELExpr)
}

fun escapeXml11(xmlELExpr: String, elEval: Boolean = true): String {
    require(xmlELExpr.isNotBlank()) { "xmlELExpr cannot be empty" }
    return chutneyFunction("escapeXml11", elEval, xmlELExpr)
}

fun unescapeXml(xmlELExpr: String, elEval: Boolean = true): String {
    require(xmlELExpr.isNotBlank()) { "xmlELExpr cannot be empty" }
    return chutneyFunction("unescapeXml", elEval, xmlELExpr)
}

fun escapeHtml3(htmlELExpr: String, elEval: Boolean = true): String {
    require(htmlELExpr.isNotBlank()) { "htmlELExpr cannot be empty" }
    return chutneyFunction("escapeHtml3", elEval, htmlELExpr)
}

fun unescapeHtml3(htmlELExpr: String, elEval: Boolean = true): String {
    require(htmlELExpr.isNotBlank()) { "htmlELExpr cannot be empty" }
    return chutneyFunction("unescapeHtml3", elEval, htmlELExpr)
}

fun escapeHtml4(htmlELExpr: String, elEval: Boolean = true): String {
    require(htmlELExpr.isNotBlank()) { "htmlELExpr cannot be empty" }
    return chutneyFunction("escapeHtml4", elEval, htmlELExpr)
}

fun unescapeHtml4(htmlELExpr: String, elEval: Boolean = true): String {
    require(htmlELExpr.isNotBlank()) { "htmlELExpr cannot be empty" }
    return chutneyFunction("unescapeHtml4", elEval, htmlELExpr)
}

fun escapeSql(sqlELExpr: String, elEval: Boolean = true): String {
    require(sqlELExpr.isNotBlank()) { "sqlELExpr cannot be empty" }
    return chutneyFunction("escapeSql", elEval, sqlELExpr)
}
