/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { DistinctPipe } from './distinct.pipe';

describe('DistinctPipe', () => {
  it('create an instance', () => {
    const pipe = new DistinctPipe();
    expect(pipe).toBeTruthy();
  });
});
