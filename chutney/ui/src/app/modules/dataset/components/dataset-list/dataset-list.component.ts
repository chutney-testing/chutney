/**
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { newInstance } from '@shared/tools';
import { distinct, flatMap } from '@shared/tools/array-utils';
import { DataSetService } from '@core/services';
import { Authorization, Dataset } from '@model';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { DROPDOWN_SETTINGS } from '@core/model/dropdown-settings';
import { IDropdownSettings } from 'ng-multiselect-dropdown';

@Component({
    selector: 'chutney-dataset-list',
    templateUrl: './dataset-list.component.html',
    styleUrls: ['./dataset-list.component.scss']
})
export class DatasetListComponent implements OnInit, OnDestroy {

    datasets: Array<Dataset> = [];

    preview: Dataset = null;

    dataSetFilter = '';
    itemList = [];
    selectedTags: string[] = [];
    selectedItem: any[];
    urlParams: Subscription;

    Authorization = Authorization;

    constructor(
        private router: Router,
        private dataSetService: DataSetService,
        private readonly route: ActivatedRoute,
        @Inject(DROPDOWN_SETTINGS) public dropdownSettings: IDropdownSettings
    ) {}

    ngOnInit(): void {
        this.dataSetService.findAll().subscribe(
            (res) => {
                this.datasets = res;
                this.initTags();
                this.applyUriState();
            },
            (error) => console.log(error)
        );
    }

    ngOnDestroy(): void {
        if (this.urlParams) {
            this.urlParams.unsubscribe();
        }
    }

    showPreview(dataset: Dataset) {
        if (this.preview == null || this.preview.id !== dataset.id) {
            this.dataSetService.findById(dataset.id).subscribe(
                (res) => {
                    this.preview = res;
                },
                (error) => console.log(error)
            );
        } else {
            this.preview = null;
        }
    }

    private initTags() {
        const allTagsInDataset: string[] = distinct(flatMap(this.datasets, (sc) => sc.tags)).sort();
        let index = 0;
        this.itemList = allTagsInDataset.map(t => {
            index++;
            return { 'id': index, 'text': t };
        });
    }

    filterSearchChange(searchFilter: string) {
        this.dataSetFilter = searchFilter;
        this.applyFiltersToRoute();
    }

    onItemSelect(item: any) {
        this.selectedTags.push(item.text);
        this.selectedTags = newInstance(this.selectedTags);
    }

    onItemDeSelect(item: any) {
        this.selectedTags.splice(this.selectedTags.indexOf(item.text), 1);
        this.selectedTags = newInstance(this.selectedTags);
    }

    onItemDeSelectAll() {
        this.selectedTags = newInstance([]);
    }

    applyFiltersToRoute() {
        this.router.navigate([], {
            relativeTo: this.route,
            queryParams: {
                text: this.dataSetFilter ? this.dataSetFilter : null,
                tags: this.selectedItem?.length ? this.selectedItem.map((i) => i.text).toString() : null
            }
        });
    }

    private applyUriState() {
        this.urlParams = this.route.queryParams
            .pipe(map((params: Array<any>) => {
                    if (params['text']) {
                        this.dataSetFilter = params['text'];
                    }
                    if (params['tags']) {
                        const uriTag = params['tags'].split(',');
                        if (uriTag != null) {
                            this.selectedItem = this.itemList.filter((tagItem) => uriTag.includes(tagItem.text));
                            this.selectedTags = this.selectedItem.map((i) => i.text);
                            this.applyFiltersToRoute();
                        }
                    }
                }))
            .subscribe();
    }
}
