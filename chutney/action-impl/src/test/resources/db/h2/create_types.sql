/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

CREATE TABLE IF NOT EXISTS allsqltypes (
  col_boolean BIT,
  col_smallint SMALLINT,
  col_integer INTEGER,
  col_float REAL,
  col_double DOUBLE,
  col_decimal DECIMAL(20,4),
  col_date DATE,
  col_time TIME,
  col_timestamp TIMESTAMP,
  col_interval_year INTERVAL YEAR,
  col_interval_second INTERVAL SECOND,
  col_char CHAR,
  col_varchar VARCHAR,
  col_blob BLOB
);


INSERT INTO allsqltypes VALUES (
                                   1,
                                   66,
                                   66666666,
                                   666.666,
                                   66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666.666,
                                   6666666666.6666,
                                   '1966-06-06',
                                   '06:16:16',
                                   '1966-06-06 06:16:16',
                                   INTERVAL '66' YEAR,
                                   INTERVAL '6' SECOND,
                                   'H',
                                   'H HHH',
                                   CAST(X'436875746e657920697320612066756e6e7920746f6f6c2e' AS BLOB)
                               );
