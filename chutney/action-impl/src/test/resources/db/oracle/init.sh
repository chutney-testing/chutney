#
# SPDX-FileCopyrightText: 2017-2024 Enedis
#
# SPDX-License-Identifier: Apache-2.0
#
#

sqlplus -s testUser/testPassword@//localhost/testDB @/sql/create_users.sql
sqlplus -s testUser/testPassword@//localhost/testDB @/sql/create_types.sql
