/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';


@Injectable()
export class ValidationService {

    private urlRegex = new RegExp('^[a-z][a-z0-9\+-\.]*:\/\/[^:]+(:[0-9]+)?.*$');
    private spelRegex = new RegExp('\\$\\{([^}]+)\\}');
    private envNameRegex = new RegExp('^[a-zA-Z0-9_-]{3,20}$');
    private varNameRegex = new RegExp('^[a-zA-Z][a-zA-Z_0-9]*$');

    constructor() { }

    isNotEmpty(text: string): boolean {
        return text != null && text.trim() !== '';
    }

    isValidUrl(text: string): boolean {
        return this.urlRegex.test(text);
    }

    isValidSpel(text: string): boolean {
        return this.spelRegex.test(text);
    }

    isValidUrlOrSpel(text: string): boolean {
        return this.isValidUrl(text) || this.isValidSpel(text);
    }
    isValidEnvName(text: string): boolean {
        return text !== null && this.envNameRegex.test(text);
    }

    isValidVariableName(text: string): boolean {
        return text !== null && this.varNameRegex.test(text);
    }

    isValidPattern(text: string) {
        try {
            new RegExp(text);
        } catch {
            return false;
        }
        return true;
    }

    asValidatorFn(fn: (value: any) => boolean, errorName: string) : ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const valid = fn(control.value);
            return valid ? null : { [errorName]: true };
        };
    }
}
