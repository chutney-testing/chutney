/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
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
