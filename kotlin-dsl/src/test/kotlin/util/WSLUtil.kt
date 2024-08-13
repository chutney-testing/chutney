/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package util

import org.apache.commons.lang3.SystemUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.pathString

object WSLUtil {
    fun wslPath(path: Path): String {
        if (SystemUtils.IS_OS_WINDOWS) {
            val absolutePath = path.toAbsolutePath()
            val t = (0..<absolutePath.nameCount).map(absolutePath::getName).map(Path::toString).toTypedArray()
            return listOf("mnt", absolutePath.root.toString().substring(0, 1), *t).joinToString("/", "/")
        }
        return path.pathString
    }
}

class WSLUtilTest {
    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun wslPath() {
        val dir = Files.createTempDirectory("wslPath-")
        assertThat(WSLUtil.wslPath(dir)).startsWith("/mnt/")
    }
}
