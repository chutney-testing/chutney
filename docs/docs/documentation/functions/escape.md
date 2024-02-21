!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/EscapeFunctions.java){:target="_blank"}"

Following functions help you escape and unescape strings from JSON, HTML, XML and SQL.

The class [StringEscapeUtils](https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/StringEscapeUtils.html){:target="_blank"}
from **Apache commons text** library is used under the hood.

# JSON

!!! note "String escapeJson(String text)"

    **Examples** :

    SpEL : `${#escapeJson("text")}`

!!! note "String unescapeJson(String text)"

    **Examples** :

    SpEL : `${#unescapeJson("text")}`

# HTML

!!! note "String escapeHtml3(String text)"

    **Examples** :

    SpEL : `${#escapeHtml13("text")}`

!!! note "String escapeHtml4(String text)"

    **Examples** :

    SpEL : `${#escapeHtml14("text")}`

!!! note "String unescapeHtml3(String text)"

    **Examples** :

    SpEL : `${#unescapeHtml3("text")}`

!!! note "String unescapeHtml4(String text)"

    **Examples** :

    SpEL : `${#unescapeHtml4("text")}`

# SQL

!!! note "String escapeSql(String sql)"

    **Examples** :

    SpEL : `${#escapeSql("sql")}`

# XML

!!! note "String unescapeXml(String text)"

    **Examples** :

    SpEL : `${#unescapeXml("text")}`

!!! note "String escapeXml10(String text)"

    **Examples** :

    SpEL : `${#escapeXml10("text")}`

!!! note "String escapeXml11(String text)"

    **Examples** :

    SpEL : `${#escapeXml11("text")}`

