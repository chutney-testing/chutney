/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package util

import org.junitpioneer.jupiter.ClearSystemProperty
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited

@ClearSystemProperty(key="http.proxyHost")
@ClearSystemProperty(key="http.proxyPort")
@ClearSystemProperty(key="http.proxyUser")
@ClearSystemProperty(key="http.proxyPassword")
@ClearSystemProperty(key="https.proxyHost")
@ClearSystemProperty(key="https.proxyPort")
@ClearSystemProperty(key="https.proxyUser")
@ClearSystemProperty(key="https.proxyPassword")
annotation class ChutneyServerInfoClearProperties ()
