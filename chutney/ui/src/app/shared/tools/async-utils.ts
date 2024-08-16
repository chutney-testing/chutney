/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
