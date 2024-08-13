/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { ChutneyMainHeaderComponent } from '@shared/components/layout/header/chutney-main-header.component';
import { LoginService } from '@core/services';
import { Authorization, User } from '@model';
import { Observable, of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';
import { TranslateTestingModule } from '../../app/testing/translate-testing.module';

const mockLoginService = {
  hasAuthorization(
    authorization: Array<Authorization> | Authorization = [],
    u: User = null,
  ): boolean {
    return true;
  },
  isAuthenticated(): boolean {
    return true;
  },
  getUser(): Observable<User> {
    return of(new User("user_id", "username", "firstname"));
  },
};

export default {
  title: "Components/Main header",
  component: ChutneyMainHeaderComponent,
  decorators: [
    moduleMetadata({
      imports: [TranslateModule, TranslateTestingModule],
      providers: [{ provide: LoginService, useValue: mockLoginService }],
    }),
  ],
  args: {},
} as Meta;

type Story = StoryObj<ChutneyMainHeaderComponent>;

export const Default: Story = {};
