package com.chutneytesting.kolin.util;

import org.junitpioneer.jupiter.ClearSystemProperty;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@ClearSystemProperty(key="http.proxyHost")
@ClearSystemProperty(key="http.proxyPort")
@ClearSystemProperty(key="http.proxyUser")
@ClearSystemProperty(key="http.proxyPassword")
@ClearSystemProperty(key="https.proxyHost")
@ClearSystemProperty(key="https.proxyPort")
@ClearSystemProperty(key="https.proxyUser")
@ClearSystemProperty(key="https.proxyPassword")
public @interface ChutneyServerInfoClearProperties {
}
