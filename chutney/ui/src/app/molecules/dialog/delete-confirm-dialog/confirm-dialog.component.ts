/**
 * Copyright 2017-2023 Enedis
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

import { Component, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';

import { disabledBoolean } from '@shared/tools/bool-utils';

@Component({
    selector: 'chutney-confirm-dialog',
    templateUrl: './confirm-dialog.component.html',
    styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {

    modalRef: BsModalRef;
    @Input() dialogMessage: string= "global.confirm.delete";
    @Input() type = 'trash-button';
    @Input() label: string;
    @Input() title: string = "global.actions.delete";
    @Input() disabled = false;
    @Input() btnSizeClass: 'lg' | 'sm';
    @Input() btnClassIcon: string= "fa-trash";
    @Input() btnColor: string;
    @Output() callbackEvent = new EventEmitter();
    disabledBoolean = disabledBoolean;

    constructor(private modalService: BsModalService) {
    }

    openModal(template: TemplateRef<any>) {
        this.modalRef = this.modalService.show(template, {class: 'modal-sm'});
        document.getElementById('no-btn').focus();
    }

    confirm(): void {
        this.modalRef.hide();
        this.callbackEvent.emit();
    }

    decline(): void {
        this.modalRef.hide();
    }
}
