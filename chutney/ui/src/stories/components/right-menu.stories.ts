/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { Authorization, User } from '@model';
import { SharedModule } from '@shared/shared.module';
import { HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { APP_BASE_HREF } from '@angular/common';
import { TranslateTestingModule } from '../../app/testing/translate-testing.module';
import { expect, fn, userEvent, waitFor, within } from '@storybook/test';
import { ChutneyRightMenuComponent } from '@shared/components/layout/right-menu/chutney-right-menu.component';
import { LoginService } from '@core/services';
import { intersection } from '@shared/tools';
import { ActivatedRouteStub } from '../../app/testing/activated-route-stub';

const mockLoginService = {
  hasAuthorization(
    authorization: Array<Authorization> | Authorization = [],
    u: User = null,
  ) {
    return (
      !authorization.length ||
      intersection([Authorization.SCENARIO_EXECUTE], [...authorization]).length
    );
  },
};

const meta: Meta<ChutneyRightMenuComponent> = {
  title: "Components/Right menu",
  component: ChutneyRightMenuComponent,
  decorators: [
    moduleMetadata({
      imports: [
        RouterModule.forChild([]),
        SharedModule,
        HttpClientModule,
        TranslateModule,
        TranslateTestingModule,
      ],
      providers: [
          {
              provide: ActivatedRoute, useClass: ActivatedRouteStub
          },
        {
          provide: APP_BASE_HREF,
          useValue: "/",
        },
        {
          provide: LoginService,
          useValue: mockLoginService,
        },
      ],
    }),
  ],
};
export default meta;
type Story = StoryObj<ChutneyRightMenuComponent>;

export const EmptyMenu: Story = {
  args: {
    menuItems: [],
  },
};

export const TranslatedItemLabel: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
      },
    ],
  },
};

export const ItemWithIcon: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
      },
    ],
  },
};

export const ItemWithLink: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
        link: "/",
      },
    ],
  },
};

export const ItemWithClickCallback: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
        click: fn(),
      },
    ],
  },
  play: async ({ args, canvasElement }) => {
    const canvas = within(canvasElement);

    await userEvent.click(canvas.getByRole("nav-link"));
    console.log(args.menuItems[0]);

    await waitFor(() => expect(args.menuItems[0].click).toHaveBeenCalled());
  },
};

export const DropDownItem: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.execute",
        iconClass: "fa fa-play",
        options: [
          { id: "1", label: "env 1" },
          { id: "2", label: "env 2" },
        ],
        click: fn(),
      },
    ],
  },
};

export const EmptyIfNotAuthorized: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
        authorizations: [Authorization.SCENARIO_WRITE],
      },
    ],
  },
};
export const MenuWithManyItems: Story = {
  args: {
    menuItems: [
      {
        label: "global.actions.execute",
        iconClass: "fa fa-play",
        options: [
          { id: "1", label: "env 1" },
          { id: "2", label: "env 2" },
        ],
        click: fn(),
      },
      {
        label: "global.actions.edit",
        iconClass: "fa fa-pencil-alt",
        link: "/",
      },
    ],
  },
};
