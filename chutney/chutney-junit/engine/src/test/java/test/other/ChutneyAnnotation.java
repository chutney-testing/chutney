/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package test.other;

import com.chutneytesting.junit.api.AfterAll;
import com.chutneytesting.junit.api.BeforeAll;
import com.chutneytesting.junit.api.Chutney;

@Chutney
public class ChutneyAnnotation {

    @BeforeAll
    public void setUp() {
        // Nothing to do
    }

    @AfterAll
    public void tearDown() {
        // Nothing to do
    }

}
