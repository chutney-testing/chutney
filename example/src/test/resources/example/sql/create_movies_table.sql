/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

CREATE TABLE MOVIES (
    ID SERIAL PRIMARY KEY,
    TITLE varchar(50),
    DIRECTOR varchar(50),
    RELEASED_AT varchar(50),
    RATING varchar(3)
);
