/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.sql.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.chutneytesting.action.TestTarget;
import com.chutneytesting.action.spi.injectable.Target;
import com.chutneytesting.tools.ChutneyMemoryInfo;
import com.chutneytesting.tools.NotEnoughMemoryException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.MountableFile;

public class SqlClientTest {

    @Nested
    class H2SqlClientTest extends AllTests {
        @BeforeAll
        static void beforeAll() {
            sqlTarget = TestTarget.TestTargetBuilder.builder()
                .withTargetId("sql")
                .withUrl("jdbc:h2:mem")
                .withProperty("jdbcUrl", "jdbc:h2:mem:" + DB_NAME)
                .withProperty("user", "sa")
                .build();
        }

        @BeforeEach
        public void setUp() {
            new EmbeddedDatabaseBuilder()
                .setName(DB_NAME)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScripts("db/common/create_users.sql", "db/h2/create_types.sql")
                .build();
        }

    }

    @Nested
    class OracleSqlClientTest extends AllTests {
        private static OracleContainer oracle = new OracleContainer("gvenzl/oracle-free:23.5-slim-faststart")
            .withDatabaseName("testDB")
            .withUsername("testUser")
            .withPassword("testPassword")
            .withCopyFileToContainer(MountableFile.forClasspathResource("db/oracle/init.sh"), "/container-entrypoint-initdb.d/init.sh")
            .withCopyFileToContainer(MountableFile.forClasspathResource("db/oracle/create_types.sql"), "/sql/create_types.sql")
            .withCopyFileToContainer(MountableFile.forClasspathResource("db/common/create_users.sql"), "/sql/create_users.sql");

        @BeforeAll
        static void beforeAll() {
            oracle.start();
            String address = oracle.getHost();
            Integer port = oracle.getFirstMappedPort();
            sqlTarget = TestTarget.TestTargetBuilder.builder()
                .withTargetId("sql")
                .withUrl("jdbc:oracle:thin:@" + address + ":" + port + "/testDB")
                .withProperty("user", "testUser")
                .withProperty("password", "testPassword")
                .build();
        }

        @AfterAll
        static void afterAll() {
            oracle.stop();
        }
    }

    abstract static class AllTests {
        protected static final String DB_NAME = "test_" + SqlClientTest.class;
        protected static Target sqlTarget;


        @Test
        public void should_return_headers_and_rows_on_select_query() throws SQLException {

            SqlClient sqlClient = new DefaultSqlClientFactory().create(sqlTarget);
            Records actual = sqlClient.execute("select * from users where ID = 1");

            assertThat(actual.getHeaders()).containsOnly("ID", "NAME", "EMAIL");
            assertThat(actual.records).hasSize(1);
            assertThat(actual.records.get(0)).isNotNull();
            List<Cell> firstRowCells = actual.records.get(0).cells;
            assertThat(firstRowCells).hasSize(3);
            assertThat(firstRowCells.get(0).column.name).isEqualTo("ID");
            assertThat(((Number) firstRowCells.get(0).value).intValue()).isEqualTo(1);
            assertThat(firstRowCells.get(1).column.name).isEqualTo("NAME");
            assertThat(firstRowCells.get(1).value).isEqualTo("laitue");
            assertThat(firstRowCells.get(2).column.name).isEqualTo("EMAIL");
            assertThat(firstRowCells.get(2).value).isEqualTo("laitue@fake.com");
        }

        @Test
        public void should_return_affected_rows_on_update_queries() throws SQLException {
            SqlClient sqlClient = new DefaultSqlClientFactory().create(sqlTarget);
            Records records = sqlClient.execute("UPDATE USERS SET NAME = 'toto' WHERE ID = 1");

            assertThat(records.affectedRows).isEqualTo(1);
        }

        @Test
        public void should_return_count_on_count_queries() throws SQLException {

            SqlClient sqlClient = new DefaultSqlClientFactory().create(sqlTarget);
            Records actual = sqlClient.execute("SELECT COUNT(*) as total FROM USERS");

            assertThat(actual.records).hasSize(1);
            assertThat(actual.records.get(0)).isNotNull();
            assertThat(actual.records.get(0).cells).hasSize(1);
            assertThat(actual.records.get(0).cells.get(0)).isNotNull();
            Number count = (Number) actual.records.get(0).cells.get(0).value;
            assertThat(count.intValue()).isEqualTo(3);
            assertThat(actual.records.get(0).cells.get(0).column.name).isEqualTo("TOTAL");
        }

        @Test
        public void should_retrieve_columns_as_expected_datatypes() throws SQLException {
            SqlClient sqlClient = new DefaultSqlClientFactory().create(sqlTarget);
            Records actual = sqlClient.execute("select * from allsqltypes");

            Row firstRow = actual.rows().get(0);
            assertThat(firstRow.get("COL_BOOLEAN")).isInstanceOf(Boolean.class);
            assertThat(firstRow.get("COL_INTEGER")).isInstanceOfAny(Integer.class, BigDecimal.class);
            assertThat(firstRow.get("COL_FLOAT")).isInstanceOf(Float.class);
            assertThat(firstRow.get("COL_DOUBLE")).isInstanceOf(Double.class);
            assertThat(firstRow.get("COL_DECIMAL")).isInstanceOf(BigDecimal.class);
            assertThat(firstRow.get("COL_DATE")).isInstanceOfAny(Date.class, Timestamp.class);
            assertThat(firstRow.get("COL_TIME")).isInstanceOfAny(Time.class, String.class);
            assertThat(firstRow.get("COL_TIMESTAMP")).isInstanceOfAny(Timestamp.class, String.class);
            assertThat(firstRow.get("COL_CHAR")).isInstanceOf(String.class);
            assertThat(firstRow.get("COL_VARCHAR")).isInstanceOf(String.class);
            assertThat(firstRow.get("COL_INTERVAL_YEAR")).isInstanceOf(String.class);
            assertThat(firstRow.get("COL_INTERVAL_SECOND")).isInstanceOf(String.class);
            assertThat(firstRow.get("COL_BLOB")).isInstanceOf(String.class);
            assertThat(firstRow.get("COL_BLOB")).isEqualTo("Chutney is a funny tool.");
        }

        @Test
        public void should_prevent_out_of_memory() {
            try (MockedStatic<ChutneyMemoryInfo> chutneyMemoryInfoMockedStatic = Mockito.mockStatic(ChutneyMemoryInfo.class)) {
                chutneyMemoryInfoMockedStatic.when(ChutneyMemoryInfo::hasEnoughAvailableMemory).thenReturn(true, true, false);
                chutneyMemoryInfoMockedStatic.when(ChutneyMemoryInfo::usedMemory).thenReturn(42L * 1024 * 1024);
                chutneyMemoryInfoMockedStatic.when(ChutneyMemoryInfo::maxMemory).thenReturn(1337L * 1024 * 1024);

                SqlClient sqlClient = new DefaultSqlClientFactory().create(sqlTarget);

                Exception exception = assertThrows(NotEnoughMemoryException.class, () -> sqlClient.execute("select * from users"));
                assertThat(exception.getMessage()).isEqualTo("Running step was stopped to prevent application crash. 42MB memory used of 1337MB max.\n" +
                    "Current step may not be the cause.\n" +
                    "Query fetched 2 rows");
            }
        }
    }
}
