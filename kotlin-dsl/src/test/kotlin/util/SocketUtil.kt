/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package util

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.Semaphore

object SocketUtil {
    private val lock = Semaphore(1)

    fun freePort(): Int {
        try {
            lock.acquire()
            ServerSocket(0).use { socket ->
                socket.reuseAddress = true
                return socket.localPort
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            lock.release()
        }
    }
}
