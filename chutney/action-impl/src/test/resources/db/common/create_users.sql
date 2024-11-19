/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
DROP TABLE users;

CREATE TABLE users (
  id INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email VARCHAR(50)
);

INSERT INTO users VALUES (1, 'laitue', 'laitue@fake.com');
INSERT INTO users VALUES (2, 'carotte', 'kakarot@fake.db');
INSERT INTO users VALUES (3, 'tomate', null);
