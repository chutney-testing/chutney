/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { componentWrapperDecorator, Meta, StoryObj } from '@storybook/angular';
import { ExecutionBadgeComponent } from '@shared/components';

const meta: Meta<ExecutionBadgeComponent> = {
  title: "Components/Execution Badge",
  component: ExecutionBadgeComponent,
  excludeStories: /^Default$/,
  decorators: [
    componentWrapperDecorator(
      (story) => `<div class="bg-dark">${story}</div>`,
    ),
  ],
  args: {
    status: "SUCCESS",
    spin: false,
  },
};

 export default meta;

type Story = StoryObj<ExecutionBadgeComponent>;

export const Default: Story = {
  args: {
    // args are taken from component level args.
  },
};

export const Success: Story = {
  args: {
    ...Default.args,
  },
};
export const Failure: Story = {
  args: {
    ...Default.args,
    status: "FAILURE",
  },
};
export const Running: Story = {
  args: {
    ...Default.args,
    status: "RUNNING",
  },
};
export const Spinning: Story = {
  args: {
    ...Running.args,
    spin: true,
  },
};
export const Paused: Story = {
  args: {
    ...Default.args,
    status: "PAUSED",
  },
};

export const Stopped: Story = {
  args: {
    ...Default.args,
    status: "STOPPED",
  },
};

export const NotExecuted: Story = {
  args: {
    ...Default.args,
    status: "NOT_EXECUTED",
  },
};
