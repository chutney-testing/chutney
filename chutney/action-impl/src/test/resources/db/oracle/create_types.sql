/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

CREATE TABLE allsqltypes (
 col_boolean BOOLEAN,
 col_smallint SMALLINT,
 col_integer INTEGER,
 col_float BINARY_FLOAT,
 col_double BINARY_DOUBLE,
 col_decimal NUMBER(20, 4),
 col_date DATE,
 col_time TIMESTAMP,
 col_timestamp TIMESTAMP,
 col_interval_year INTERVAL YEAR TO MONTH,
 col_interval_second INTERVAL DAY TO SECOND,
 col_char CHAR(1),
 col_varchar VARCHAR2(255),
 col_blob BLOB
);

INSERT INTO allsqltypes VALUES (
    1,
    66666,
    66666666,
    666.666,
    66666666666666666666666666666666666666666666666666666666666666666666666666666666666.666,
    6666666666.6666,
    to_date('1966-06-06', 'YYYY-MM-DD'),
    to_timestamp('1966-06-06 06:16:16', 'YYYY-MM-DD HH24:MI:SS'),
    to_date('1966-06-06 06:16:16', 'YYYY-MM-DD HH24:MI:SS'),
    INTERVAL '66' YEAR,
    INTERVAL '6' SECOND,
    'H',
    'H HHH',
    '436875746e657920697320612066756e6e7920746f6f6c2e'
);
