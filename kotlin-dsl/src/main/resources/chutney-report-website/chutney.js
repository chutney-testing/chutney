
/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

class Chutney {

    constructor() {
        this.reportListElement = document.getElementById('report-list');
        this.reportSuccessTemplate = document.getElementById('report-success-template');
        this.reportFailureTemplate = document.getElementById('report-failure-template');
        this.reportEnvironmentTemplate = document.getElementById('report-env-template');
    }

    fillReportList(reportList) {
        reportList.forEach(envReport => {
            fetchAsJson.bind(this)(`${envReport.env}/${envReport.scenario}`)
                .then(report => this.addReport(envReport.env, report))
        }, this);
    }

    addReport(env, report) {
        const status = report.status;
        const templateToUse = status === 'SUCCESS' ? this.reportSuccessTemplate : this.reportFailureTemplate;
        var templateClone = document.importNode(templateToUse.content, true);
        var div = templateClone.querySelector('div');
        div.textContent = report.name;

        var envTemplateClone = document.importNode(this.reportEnvironmentTemplate.content, true);
        var envDiv = envTemplateClone.querySelector('div');
        envDiv.textContent = env;

        div.appendChild(envDiv);

        this.reportListElement.appendChild(div);
    }
}

function fetchAsJson(name) {
    return fetch(name)
        .then( res => res.json() )
        .catch( err => {
            console.log('Problem fetching the file ' + name);
            console.log(err);
            alert('Problem fetching the file ' + name);
        });
}

function escapeHTML(str) {
    var p = document.createElement("p");
    p.appendChild(document.createTextNode(str));
    return p.innerHTML;
}

window.addEventListener("DOMContentLoaded", e => {
    chutney = new Chutney();
    fetchAsJson('reports-list.json')
        .then( res => chutney.fillReportList(res) );
});
