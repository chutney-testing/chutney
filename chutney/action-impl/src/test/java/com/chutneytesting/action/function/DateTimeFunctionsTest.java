/*
 * Copyright 2017-2024 Enedis
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

package com.chutneytesting.action.function;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.zone.ZoneRules;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Date and Time Functions")
public class DateTimeFunctionsTest {

    @Nested
    @DisplayName("date function parses a Temporal instance corresponding to the given format")
    class DateFunction {
        @Test
        @DisplayName("Instant if no format")
        void date_parses_to_Instant_when_no_format_provided() {
            Instant i = (Instant) DateTimeFunctions.date("2018-01-15T14:38:21Z");
            assertThat(i.getEpochSecond()).isEqualTo(1516027101L);
        }

        @Test
        @DisplayName("LocalDate")
        void date_parses_to_LocalDate_when_date_format_provided() {
            Temporal i = DateTimeFunctions.date("2018-01-15", "yyyy-MM-dd");
            assertThat(i).asInstanceOf(InstanceOfAssertFactories.LOCAL_DATE)
                .hasYear(2018)
                .hasMonth(Month.JANUARY)
                .hasDayOfMonth(15);
        }

        @Test
        @DisplayName("ZonedDateTime")
        void date_parses_to_ZonedDateTime_when_zoned_date_time_format_provided() {
            Temporal i = DateTimeFunctions.date("2018-01-15T14:38:21+0200", "yyyy-MM-dd'T'HH:mm:ssx");
            assertThat(i).asInstanceOf(InstanceOfAssertFactories.ZONED_DATE_TIME)
                .satisfies(ld -> assertThat(ld.toEpochSecond()).isEqualTo(1516019901L));
        }

        @Test
        @DisplayName("LocalDateTime")
        void date_parses_to_LocalDateTime_when_date_time_format_provided() {
            Temporal i = DateTimeFunctions.date("2018-01-15T14:38:21", "yyyy-MM-dd'T'HH:mm:ss");
            assertThat(i).asInstanceOf(InstanceOfAssertFactories.LOCAL_DATE_TIME)
                .satisfies(ldt -> assertThat(ldt.toEpochSecond(ZoneOffset.ofHours(2))).isEqualTo(1516019901L));
        }
    }

    @Test
    @DisplayName("now function gets current time as ZonedDateTime")
    void now_gets_current_time_as_ZonedDateTime() {
        ZonedDateTime now = DateTimeFunctions.now();
        assertThat(Instant.from(now).toEpochMilli()).isCloseTo(System.currentTimeMillis(), Offset.offset(200L));
    }

    @Test
    @DisplayName("currentTimeMillis function gets current time milliseconds as String")
    void currentTimeMillis_gets_current_time_milliseconds_as_String() {
        String now = DateTimeFunctions.currentTimeMillis();
        assertThat(Long.valueOf(now)).isCloseTo(System.currentTimeMillis(), Offset.offset(200L));
    }

    @Nested
    @DisplayName("dateFormatter functions should build a DateTimeFormatter")
    class DateFormatterFunctions {
        @Test
        @DisplayName("from pattern")
        void dateFormatter_builds_DateTimeFormatter_from_pattern() {
            assertDoesNotThrow(() -> DateTimeFunctions.dateFormatter("yyyy"));
        }

        @Test
        @DisplayName("from pattern and locale")
        void dateFormatterWithLocale_builds_DateTimeFormatter_from_pattern_and_locale() {
            assertDoesNotThrow(() -> DateTimeFunctions.dateFormatterWithLocale("yyyy", "fr_FR"));
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.chutneytesting.action.function.DateTimeFunctionsTest#isoDateFormatter")
        @DisplayName("from ISO type")
        void isoDateFormatter_builds_DateTimeFormatter_from_iso_type(String type, DateTimeFormatter expectedIsoDTF) {
            assertSame(expectedIsoDTF, DateTimeFunctions.isoDateFormatter(type));
        }
    }

    @Nested
    @DisplayName("timeAmount function")
    class TimeAmountFunction {
        @Nested
        @DisplayName("parses a TimeAmount")
        class ValidParse {
            @Test
            @DisplayName("from Chutney Duration format")
            void timeAmount_parses_a_TimeAmount_from_a_chutney_duration() {
                TemporalAmount ta = DateTimeFunctions.timeAmount("2 ms");
                assertThat(ta).asInstanceOf(InstanceOfAssertFactories.DURATION)
                    .returns(2L, Duration::toMillis);
            }

            @Test
            @DisplayName("from Java time Duration format")
            void timeAmount_parses_a_TimeAmount_from_java_time_duration() {
                TemporalAmount ta = DateTimeFunctions.timeAmount("P4DT2H5M3.123456789S");
                assertThat(ta).asInstanceOf(InstanceOfAssertFactories.DURATION)
                    .returns(353103L, Duration::getSeconds)
                    .returns(123456789, Duration::getNano);
            }

            @Test
            @DisplayName("from Java time Period format")
            void timeAmount_parses_a_TimeAmount_from_java_time_period() {
                TemporalAmount ta = DateTimeFunctions.timeAmount("P2Y3M6W5D");
                assertThat(ta).asInstanceOf(InstanceOfAssertFactories.PERIOD)
                    .hasYears(2)
                    .hasMonths(3)
                    .hasDays(47);
            }
        }

        @Test
        void timeAmount_throws_IllegalArgument_when_text_not_parsable() {
            assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeFunctions.timeAmount("unparsable time amount text")
            );
        }
    }

    @Nested
    @DisplayName("timeUnit function")
    class TimeUnitFunction {
        @Nested
        @DisplayName("parses a ChronoUnit")
        class ValidParse {
            @Test
            @DisplayName("from Chutney DurationUnit format")
            void timeUnit_parses_a_TimeAmount_from_a_chutney_duration_unit() {
                ChronoUnit cu = DateTimeFunctions.timeUnit("days");
                assertEquals(ChronoUnit.DAYS, cu);
            }

            @ParameterizedTest(name = "{0}")
            @EnumSource(ChronoUnit.class)
            @DisplayName("from Java time ChronoUnit enum case insensitive")
            void timeUnit_parses_a_TimeAmount_from_java_time_ChronoUnit_enum(ChronoUnit cu) {
                boolean randBool = (int) (Math.random() * 10) % 2 == 0;
                ChronoUnit tu = DateTimeFunctions.timeUnit(randBool ? cu.name() : cu.name().toLowerCase());
                assertEquals(cu, tu);
            }
        }

        @Test
        void timeUnit_throws_IllegalArgument_when_text_not_parsable() {
            assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeFunctions.timeUnit("unparsable time unit text")
            );
        }
    }

    @Nested
    @DisplayName("zoneRules functions")
    class ZoneRulesFunctions {
        @ParameterizedTest
        @ValueSource(strings = {"Z", "GTM", "+01:00", "Europe/Paris"})
        void zoneRules_gets_time_rules_from_zoneId() {
            assertThat(DateTimeFunctions.zoneRules("Z")).isNotNull();
        }

        @Test
        void systemZoneRules_gets_default_system_time_rules() {
            ZoneRules systemZoneRules = DateTimeFunctions.systemZoneRules();
            assertThat(systemZoneRules).isNotNull();
            assertThat(DateTimeFunctions.zoneRules(null)).isEqualTo(systemZoneRules);
            assertThat(DateTimeFunctions.zoneRules("")).isEqualTo(systemZoneRules);
            assertThat(DateTimeFunctions.zoneRules("   ")).isEqualTo(systemZoneRules);
        }
    }

    private static Stream<Arguments> isoDateFormatter() {
        return Stream.of(
            Arguments.of("INSTANT", DateTimeFormatter.ISO_INSTANT),
            Arguments.of("ZONED_DATE_TIME", DateTimeFormatter.ISO_ZONED_DATE_TIME),
            Arguments.of("DATE_TIME", DateTimeFormatter.ISO_DATE_TIME),
            Arguments.of("DATE", DateTimeFormatter.ISO_DATE),
            Arguments.of("TIME", DateTimeFormatter.ISO_TIME),
            Arguments.of("LOCAL_DATE_TIME", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            Arguments.of("LOCAL_DATE", DateTimeFormatter.ISO_LOCAL_DATE),
            Arguments.of("LOCAL_TIME", DateTimeFormatter.ISO_LOCAL_TIME),
            Arguments.of("OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            Arguments.of("OFFSET_DATE", DateTimeFormatter.ISO_OFFSET_DATE),
            Arguments.of("OFFSET_TIME", DateTimeFormatter.ISO_OFFSET_TIME),
            Arguments.of("ORDINAL_DATE", DateTimeFormatter.ISO_ORDINAL_DATE),
            Arguments.of("ISO_WEEK_DATE", DateTimeFormatter.ISO_WEEK_DATE),
            Arguments.of("BASIC_DATE", DateTimeFormatter.BASIC_ISO_DATE),
            Arguments.of("RFC_DATE_TIME", DateTimeFormatter.RFC_1123_DATE_TIME)
        );
    }
}
