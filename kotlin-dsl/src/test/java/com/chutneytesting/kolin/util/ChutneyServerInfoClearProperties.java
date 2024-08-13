/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kolin.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junitpioneer.jupiter.ClearSystemProperty;

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
