/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { TestBed } from '@angular/core/testing';
import { ValidationService } from './validation.service';

let service: ValidationService;

beforeEach(() => {
  TestBed.configureTestingModule({ providers: [ValidationService] });
  service = TestBed.get(ValidationService);
});

afterEach(() => {
  TestBed.resetTestingModule();
});

it('isNotEmpty returns false on empty string', () => {
  expect(service.isNotEmpty('')).toBe(false);
});

it('isNotEmpty returns false on null', () => {
  expect(service.isNotEmpty(null)).toBe(false);
});

it('isNotEmpty returns true on non-empty string', () => {
  expect(service.isNotEmpty('test')).toBe(true);
});

it('isValidUrl returns false on null', () => {
  expect(service.isValidUrl(null)).toBe(false);
});

it('isValidUrl returns true on missing port URL', () => {
  expect(service.isValidUrl('test://test:')).toBe(true);
});

it('isValidUrl returns true on valid URL', () => {
  expect(service.isValidUrl('test://test:42')).toBe(true);
});

it('isValidUrl returns true on protocol with number', () => {
  expect(service.isValidUrl('t3://host:1234')).toBe(true);
});

it('isValidUrl returns false when protocol does not start with letter', () => {
  expect(service.isValidUrl('1protocol://host:1234')).toBe(false);
});

it('isValidUrl returns true on protocol with : - . +', () => {
  expect(service.isValidUrl('pro+to-co.l://host:1234')).toBe(true);
});

it('isValidUrl returns true on protocol length is 1', () => {
  expect(service.isValidUrl('p://host:1234')).toBe(true);
});

it('isValidEnvironmentName returns false on null', () => {
  expect(service.isValidEnvName(null)).toBe(false);
});

it('isValidEnvironmentName returns false on empty string', () => {
  expect(service.isValidEnvName('')).toBe(false);
});

it('isValidEnvironmentName returns true on lower case', () => {
  expect(service.isValidEnvName('test')).toBe(true);
});

it('isValidEnvironmentName returns false on space', () => {
  expect(service.isValidEnvName('TEST TEST')).toBe(false);
});

it('isValidEnvironmentName returns false short string', () => {
  expect(service.isValidEnvName('TE')).toBe(false);
});

it('isValidEnvironmentName returns true when valid environment name', () => {
  expect(service.isValidEnvName('TEST_45-2')).toBe(true);
});

it('isValidSpel returns false on null', () => {
    expect(service.isValidSpel(null)).toBe(false);
});

it('isValidSpel returns false with empty spel', () => {
    expect(service.isValidSpel('${}')).toBe(false);
});

it('isValidSpel returns true on valid spel', () => {
    expect(service.isValidSpel('${test}')).toBe(true);
});
