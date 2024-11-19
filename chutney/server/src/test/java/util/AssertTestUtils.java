/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package util;

import java.util.List;
import org.assertj.core.api.SoftAssertions;

public class AssertTestUtils {

    public static void softAssertVerifies(List<Runnable> verifies) {
        SoftAssertions softly = new SoftAssertions();
        for (Runnable verify : verifies) {
            softly.assertThatCode(verify::run).doesNotThrowAnyException();
        }
        softly.assertAll();
    }
}
