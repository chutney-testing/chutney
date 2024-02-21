package com.chutneytesting.kotlin.dsl

import org.assertj.core.api.SoftAssertions
import org.assertj.core.util.Arrays
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.util.stream.Stream

class ChutneyFunctionsTest {

    companion object {
        @JvmStatic
        fun isFunctionsUseful_examples(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of(
                    "\${#jsonPath(#body, '$.object.attribute')}",
                    Arrays.array(
                        "#jsonPath(#body, '$.object.attribute')".elEval(),
                        "jsonPath(#body, '$.object.attribute')".spEL(),
                        jsonPath("body".spELVar(), "$.object.attribute")
                    )
                ),
                Arguments.of(
                    "\${#xpath(#jsonPath(#body.get(0), '$.payload'), 'boolean(//ns:xpath/obj)')}",
                    Arrays.array(
                        "#xpath(#jsonPath(#body.get(0), '$.payload'), 'boolean(//ns:xpath/obj)')".elEval(),
                        "xpath(#jsonPath(#body.get(0), '$.payload'), 'boolean(//ns:xpath/obj)')".spEL(),
                        "xpath(#jsonPath(${"body.get(0)".spELVar()}, '$.payload'), 'boolean(//ns:xpath/obj)')".spEL(),
                        "xpath(${
                            jsonPath(
                                "body.get(0)".spELVar(),
                                "$.payload",
                                false
                            )
                        }, 'boolean(//ns:xpath/obj)')".spEL(),
                        xpath(
                            jsonPath("body.get(0)".spELVar(), "$.payload", false),
                            "boolean(//ns:xpath/obj)"
                        )
                    )
                ),
                Arguments.of(
                    "\${#generate().id('PREFIX', 4).toUpperCase()}",
                    Arrays.array(
                        "#generate().id('PREFIX', 4).toUpperCase()".elEval(),
                        "generate().id('PREFIX', 4).toUpperCase()".spEL(),
                        "${generate_id("PREFIX", 4, false)}.toUpperCase()".elEval(),
                        generate_id("PREFIX", 4, false).plus(".toUpperCase()").elEval()
                    )
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("isFunctionsUseful_examples")
    fun `is functions really useful`(manualString: String, vararg alternatives: String) {
        val soft = SoftAssertions()
        alternatives.forEach {
            soft.assertThat(manualString).isEqualTo(it)
        }
        soft.assertAll()
    }

    @Test
    fun `use json function`() {
        assertELWrap(json("ctxVar"))

        assertThrows<IllegalArgumentException> { jsonPath("") }

        val unWrappedELExpr = json("ctxVar", "$.key[?(@.name='nn')]")
            .removePrefix("\${").removeSuffix("}")
        assertExpressionNotNullWhenParsed(unWrappedELExpr)
    }

    @Test
    fun `should wrap expression for evaluation by default`() {
        assertELWrap(jsonPath("json"))
        assertELWrap(jsonSerialize("obj"))

        assertELWrap(xpath("xml"))
        assertELWrap(xpathNs("xml", prefixes = mapOf("prefix" to "ns")))

        assertELWrap(getSoapBody("login", "pass", "body"))

        assertELWrap(date("date", "format"))
        assertELWrap(currentTimeMillis())
        assertELWrap(now())
        assertELWrap(dateFormatter("pattern"))
        assertELWrap(dateFormatterWithLocale("pattern", "locale"))
        assertELWrap(isoDateFormatter("type"))
        assertELWrap(timeAmount("text"))
        assertELWrap(timeUnit("unit"))

        assertELWrap(str_replace("text", "regexp", "replace"))

        assertELWrap(generate())
        assertELWrap(generate_uuid())
        assertELWrap(generate_randomLong())
        assertELWrap(generate_randomInt(9))
        assertELWrap(generate_id(prefix = "", length = 5))
        assertELWrap(generate_id(5, suffix = ""))
        assertELWrap(generate_id(prefix = "", length = 5, suffix = ""))

        assertELWrap(wiremock_extractHeadersAsMap("var"))
        assertELWrap(wiremock_extractParameters("var"))

        assertELWrap(nullable("var"))

        assertELWrap(micrometerRegistry("className"))

        assertELWrap(tcpPort())
        assertELWrap(tcpPorts(2))
        assertELWrap(tcpPortMin(2))
        assertELWrap(tcpPortMinMax(2, 5))
        assertELWrap(tcpPortsMinMax(2, 2, 5))
        assertELWrap(tcpPortRandomRange(100))
        assertELWrap(tcpPortsRandomRange(2, 100))
        assertELWrap(udpPort())
        assertELWrap(udpPorts(2))
        assertELWrap(udpPortMin(2))
        assertELWrap(udpPortMinMax(2, 5))
        assertELWrap(udpPortsMinMax(2, 2, 5))
        assertELWrap(udpPortRandomRange(100))
        assertELWrap(udpPortsRandomRange(2, 100))

        assertELWrap(resourcePath("name"))
        assertELWrap(resourcesPath("name"))
        assertELWrap(resourceContent("name"))

        assertELWrap(escapeJson("text"))
        assertELWrap(unescapeJson("text"))
        assertELWrap(escapeXml10("text"))
        assertELWrap(escapeXml11("text"))
        assertELWrap(unescapeXml("text"))
        assertELWrap(escapeHtml3("text"))
        assertELWrap(unescapeHtml3("text"))
        assertELWrap(escapeHtml4("text"))
        assertELWrap(unescapeHtml4("text"))
        assertELWrap(escapeSql("text"))

        assertELWrap(jsonMerge("obj1", "obj2"))
        assertELWrap(jsonSet("obj1", "toto", "titi"))
        assertELWrap(jsonSetMany("obj1", mapOf(Pair("toto", "titi"), Pair("tata", "tutu")).elMap()))
    }

    private fun assertELWrap(jsonPath: String) {
        assert(jsonPath.startsWith("\${") && jsonPath.endsWith("}"))
    }

    @Test
    fun `use jsonPath function`() {
        assertThrows<IllegalArgumentException> { jsonPath("") }

        assertExpressionNotNullWhenParsed(
            jsonPath("{\"key\":\"val\"}".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            jsonPath("ctxVar".spELVar, "$.key[?(@.name='nn')]", elEval = false)
        )
    }

    @Test
    fun `use jsonSerialize function`() {
        assertThrows<IllegalArgumentException> { jsonSerialize("") }

        assertExpressionNotNullWhenParsed(
            jsonSerialize("new Object()", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            jsonSerialize("ctxVar".spELVar, elEval = false)
        )
    }

    @Test
    fun `use jsonMerge function`() {
        assertThrows<IllegalArgumentException> { jsonMerge("", "") }

        assertExpressionNotNullWhenParsed(
            jsonMerge("new Object()", "new Object()", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            jsonMerge("ctxVar".spELVar, "ctxVar".spELVar, elEval = false)
        )
    }

    @Test
    fun `use jsonSet function`() {
        assertThrows<IllegalArgumentException> { jsonSet("", "toto", "tutu") }
        assertThrows<IllegalArgumentException> { jsonSet("new Object()", "", "tutu") }
        assertThrows<IllegalArgumentException> { jsonSet("new Object()", "toto", "") }

        assertExpressionNotNullWhenParsed(
            jsonSet("new Object()", "toto", "tutu", elEval = false)
        )
    }

    @Test
    fun `use jsonSetMany function`() {
        val map: HashMap<String, String> = hashMapOf("toto" to "tutu", "tata" to "titi")
        assertThrows<IllegalArgumentException> { jsonSetMany("", map.elMap()) }

        assertExpressionNotNullWhenParsed(
            jsonSetMany("new Object()", map.elMap(), elEval = false)
        )
        assertExpressionNotNullWhenParsed(
            jsonSetMany("new Object()", "{ \"toto\": \"titi\"}", elEval = false)
        )
    }

    @Test
    fun `use xpath function`() {
        assertThrows<IllegalArgumentException> { xpath("") }

        assertExpressionNotNullWhenParsed(
            xpath("<a><b attr=\"val\">inner text</b></a>".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            xpath("ctxVar".spELVar, "//b/text()", elEval = false)
        )
    }

    @Test
    fun `use xpathNs function`() {
        val prefixes = mapOf("prefix" to "ns")
        assertThrows<IllegalArgumentException> { xpathNs("", prefixes = prefixes) }
        assertThrows<IllegalArgumentException> { xpathNs("xml", prefixes = mapOf()) }

        assertExpressionNotNullWhenParsed(
            xpathNs("<a><b attr=\"val\">inner text</b></a>".elString(), prefixes = prefixes, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            xpathNs("ctxVar".spELVar, "//b/text()".elString(), prefixes, elEval = false)
        )
    }

    @Test
    fun `use getSoapBody function`() {
        assertThrows<IllegalArgumentException> { getSoapBody("", "pass", "body") }
        assertThrows<IllegalArgumentException> { getSoapBody("user", "", "body") }
        assertThrows<IllegalArgumentException> { getSoapBody("user", "pass", "") }

        assertExpressionNotNullWhenParsed(
            getSoapBody("user", "login", "<a>body</a>".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            getSoapBody("user", "login", "ctxVar".spELVar, elEval = false)
        )
    }

    @Test
    fun `use date and time functions`() {
        assertThrows<IllegalArgumentException> { date("") }
        assertThrows<IllegalArgumentException> { dateFormatter("") }
        assertThrows<IllegalArgumentException> { dateFormatterWithLocale("", "") }
        assertThrows<IllegalArgumentException> { isoDateFormatter("") }
        assertThrows<IllegalArgumentException> { timeAmount("") }
        assertThrows<IllegalArgumentException> { timeUnit("") }

        assertExpressionNotNullWhenParsed(
            date("2011-12-03T10:15:30Z".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            date("ctxVar".spELVar, "YYYYDDMM", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            currentTimeMillis(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            now(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            dateFormatter("YYYYMMDD'T'HH:mm:ssZ", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            dateFormatterWithLocale("YYYYMMDD'T'HH:mm:ssZ", "fr_FR", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            isoDateFormatter("INSTANT", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            timeAmount("2 sec", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            timeAmount("day", elEval = false)
        )
    }

    @Test
    fun `use str_replace function`() {
        assertThrows<IllegalArgumentException> { str_replace("", "regexp") }
        assertThrows<IllegalArgumentException> { str_replace("text", "") }

        assertExpressionNotNullWhenParsed(
            str_replace("text".elString(), ".*", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            str_replace("ctxVar".spELVar, ".*", "void", elEval = false)
        )
    }

    @Test
    fun `use generate functions`() {
        assertThrows<IllegalArgumentException> { generate_randomInt(0) }

        assertExpressionNotNullWhenParsed(
            generate(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_uuid(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_randomLong(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_randomInt(100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_id(prefix = "pre", length = 5, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_id(length = 5, suffix = "suf", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            generate_id(prefix = "pre", length = 5, suffix = "suf", elEval = false)
        )
    }

    @Test
    fun `use wiremock functions`() {
        assertThrows<IllegalArgumentException> { wiremock_extractHeadersAsMap("") }
        assertThrows<IllegalArgumentException> { wiremock_extractParameters("") }

        assertExpressionNotNullWhenParsed(
            wiremock_extractHeadersAsMap("ctxVar", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            wiremock_extractParameters("ctxVar", elEval = false)
        )
    }

    @Test
    fun `use nullable function`() {
        assertThrows<IllegalArgumentException> { nullable("") }

        assertExpressionNotNullWhenParsed(
            nullable("ctxVar", elEval = false)
        )
    }

    @Test
    fun `use micrometerRegistry function`() {
        assertThrows<IllegalArgumentException> { micrometerRegistry("") }

        assertExpressionNotNullWhenParsed(
            micrometerRegistry("org.registry", elEval = false)
        )
    }

    @Test
    fun `use tcpPort and udpPort functions`() {
        assertThrows<IllegalArgumentException> { tcpPorts(0) }
        assertThrows<IllegalArgumentException> { tcpPortMin(0) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(0, 1) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(1, 0) }
        assertThrows<IllegalArgumentException> { tcpPortMinMax(2, 1) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(0, 1, 3) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 0, 1) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 1, 0) }
        assertThrows<IllegalArgumentException> { tcpPortsMinMax(5, 2, 1) }
        assertThrows<IllegalArgumentException> { tcpPortRandomRange(0) }
        assertThrows<IllegalArgumentException> { tcpPortsRandomRange(0, 100) }
        assertThrows<IllegalArgumentException> { tcpPortsRandomRange(5, 0) }
        assertThrows<IllegalArgumentException> { udpPorts(0) }
        assertThrows<IllegalArgumentException> { udpPortMin(0) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(0, 1) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(1, 0) }
        assertThrows<IllegalArgumentException> { udpPortMinMax(2, 1) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(0, 1, 3) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 0, 1) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 1, 0) }
        assertThrows<IllegalArgumentException> { udpPortsMinMax(5, 2, 1) }
        assertThrows<IllegalArgumentException> { udpPortRandomRange(0) }
        assertThrows<IllegalArgumentException> { udpPortsRandomRange(0, 100) }
        assertThrows<IllegalArgumentException> { udpPortsRandomRange(5, 0) }

        assertExpressionNotNullWhenParsed(
            tcpPort(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPorts(5, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortMin(80000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortMinMax(80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortsMinMax(5, 80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortRandomRange(100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            tcpPortsRandomRange(5, 100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPort(elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPorts(5, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortMin(80000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortMinMax(80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortsMinMax(5, 80000, 81000, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortRandomRange(100, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            udpPortsRandomRange(5, 100, elEval = false)
        )
    }

    @Test
    fun `use resource functions`() {
        assertThrows<IllegalArgumentException> { resourcePath("") }
        assertThrows<IllegalArgumentException> { resourcesPath("") }
        assertThrows<IllegalArgumentException> { resourceContent("") }
        assertThrows<IllegalArgumentException> { resourceContent("path", "") }

        assertExpressionNotNullWhenParsed(
            resourcePath("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourcesPath("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourceContent("org.package.resource", elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            resourceContent("org.package.resource", Charsets.ISO_8859_1.name(), elEval = false)
        )
    }

    @Test
    fun `use escape and unescape functions`() {
        assertThrows<IllegalArgumentException> { escapeJson("") }
        assertThrows<IllegalArgumentException> { unescapeJson("") }
        assertThrows<IllegalArgumentException> { escapeXml10("") }
        assertThrows<IllegalArgumentException> { escapeXml11("") }
        assertThrows<IllegalArgumentException> { unescapeXml("") }
        assertThrows<IllegalArgumentException> { escapeHtml3("") }
        assertThrows<IllegalArgumentException> { unescapeHtml3("") }
        assertThrows<IllegalArgumentException> { escapeHtml4("") }
        assertThrows<IllegalArgumentException> { unescapeHtml4("") }

        assertExpressionNotNullWhenParsed(
            escapeJson("json".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeJson("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeJson("json".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeJson("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml10("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml10("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml11("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeXml11("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeXml("xml".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeXml("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml3("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml3("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml3("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            escapeHtml4("ctxVar".spELVar, elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("html".elString(), elEval = false)
        )

        assertExpressionNotNullWhenParsed(
            unescapeHtml4("ctxVar".spELVar, elEval = false)
        )
    }

    private val parser = SpelExpressionParser()
    private fun assertExpressionNotNullWhenParsed(elExpr: String) {
        assertNotNull(parser.parseExpression(elExpr))
    }
}

