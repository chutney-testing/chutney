/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ExecutionStatus } from '@core/model/scenario/execution-status';
import { CampaignExecutionReport, Dataset, KeyValue } from '@core/model';

export class Execution {

  public static NO_EXECUTION: Execution = new Execution(null, null, null, null, null, null, null, null, null);

  constructor(
    public duration: number,
    public status: ExecutionStatus,
    public report: string,
    public executionId: number,
    public time: Date,
    public environment: string,
    public user: string,
    public testCaseTitle: string,
    public tags: Array<string> = [],
    public info?: string,
    public error?: string,
    public scenarioId?: string,
    public campaignReport?: CampaignExecutionReport,
    public dataset?: Dataset,

  ) { }

  static deserializeExecutions(jsonObject: any): Execution[] {
    return jsonObject.map(execution => Execution.deserialize(execution));
  }

  static deserialize(jsonObject: any): Execution {
    return new Execution(
      jsonObject.duration,
      jsonObject.status,
      jsonObject.report,
      jsonObject.executionId,
      new Date(jsonObject.time),
      jsonObject.environment,
      jsonObject.user,
      jsonObject.testCaseTitle,
      jsonObject.tags,
      jsonObject.info,
      jsonObject.error,
      jsonObject.scenarioId,
      jsonObject.campaignReport,
      this.getDataset(jsonObject.dataset)
    );
  }

  private static getDataset(jsonDataset: any) {
      if (!jsonDataset) {
          return null;
      }
      const constants = jsonDataset.constants &&  Object.keys(jsonDataset.constants).map(key => new KeyValue(key,jsonDataset.constants[key]));
      const datatable = jsonDataset.datatable?.map(line => Object.keys(line).map(key => new KeyValue(key, line[key])))
      return new Dataset(
          jsonDataset.name,
          jsonDataset.description,
          jsonDataset.tags,
          jsonDataset.lastUpdated,
          constants,
          datatable,
          jsonDataset.id
      )
  }
}
