/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.design.infra.storage.plugins.linkifier;

public class LinkifierDto {
    public String pattern;
    public String link;

    public LinkifierDto() {
        this.pattern = "";
        this.link = "";
    }

    public LinkifierDto(String pattern, String link) {
        this.pattern = pattern;
        this.link = link;
    }
}
