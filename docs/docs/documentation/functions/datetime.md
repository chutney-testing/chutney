!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/DateTimeFunctions.java){:target="_blank"}"

Following functions help you write and shorten SpEL when you need to handle time or date values.

## currentTimeMillis

!!! note "String currentTimeMillis()"

    Returns a String of the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.

    See [System.currentTimeMillis()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#currentTimeMillis()){:target="_blank"} for further details

    **Returns** :

    * A String of the current time in milliseconds

    **Examples** :

    SpEL without : `${T(java.util.String).valueOf(T(java.lang.System).currentTimeMillis())}`

    SpEL with    : `${#currentTimeMillis()}`

## date

!!! note "Temporal date(String date, String... format)"

    See [Date(Temporal)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/Temporal.html){:target="_blank"}
    & [DateTimeFormatter.parseBest()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#parseBest(java.lang.CharSequence,java.time.temporal.TemporalQuery...)){:target="_blank"} for further details

    **Parameters** :

    * `String date` : The date you want to get a Temporal from
        * ex. "27 July 2022"
    * `String format` : The format used for the date (optional, default to ISO)
        * ex. "dd MMMM yyyy"

    **Returns** : The given date as a `Temporal`

    **Examples** :

    SpEL without : `${T(java.time.format.DateTimeFormatter).ofPattern(T(java.time.format.DateTimeFormatter).ISO_INSTANT).parseBest("27 July 2022", ZonedDateTime::from, LocalDateTime::from, LocalDate::from, Instant::from)}`

    SpEL with    : `${#date("27 July 2022")}`

## dateFormatter

!!! note "DateTimeFormatter dateFormatter(String pattern)"

    Creates a formatter from a given pattern.  
    ex. Pattern `d MMM uuuu` will format date `2011-12-03` to `3 Dec 2011`.

    See [DateTimeFormatter.ofPattern()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofPattern(java.lang.String)){:target="_blank"} for further details

    **Returns** :

    * A `DateTimeFormatter`
    * Or throws an IllegalArgumentException if the pattern is not valid.

    **Examples** :

    SpEL without : `${T(java.time.format.DateTimeFormatter).ofPattern("d MMM uuuu")}`

    SpEL with    : `${#dateFormatter("d MMM uuuu")}`

## dateFormatterWithLocale

!!! note "DateTimeFormatter dateFormatterWithLocale(String pattern, String locale)"

    Creates a formatter from a given pattern and given locale.

    See [DateTimeFormatter.ofPattern()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html#ofPattern(java.lang.String)){:target="_blank"} for further details

    **Returns** :

    * A `DateTimeFormatter`
    * Or throws an IllegalArgumentException if the pattern is not valid.

    **Examples** :

    SpEL without : `${T(java.time.format.DateTimeFormatter).ofPattern("d MMM uuuu", new java.util.Locale("en"))}`

    SpEL with    : `${#dateFormatterWithLocale("d MMM uuuu", "en")}`

## isoDateFormatter

!!! note "DateTimeFormatter isoDateFormatter(String type)"

    See [isoDateFormatter(DateTimeFormatter)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html){:target="_blank"} for further details

    **Parameters** :

    * Possible values are :
        * "INSTANT"
        * "ZONED_DATE_TIME"
        * "DATE_TIME"
        * "DATE"
        * "TIME"
        * "LOCAL_DATE_TIME"
        * "LOCAL_DATE"
        * "LOCAL_TIME"
        * "OFFSET_DATE_TIME"
        * "OFFSET_DATE"
        * "OFFSET_TIME"
        * "ORDINAL_DATE"
        * "ISO_WEEK_DATE"
        * "BASIC_DATE"
        * "RFC_DATE_TIME"

    **Returns** :

    * A `DateTimeFormatter`
    * Or throws an IllegalArgumentException if the value is unknown.

    **Examples** :

    SpEL without : `${T(java.time.format.DateTimeFormatter).ISO_INSTANT}`

    SpEL with    : `${#isoDateFormatter("INSTANT")}`

## now

!!! note "ZonedDateTime now()"

    Returns the current date-time from the system clock.

    See [ZonedDateTime.now()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/ZonedDateTime.html#now()){:target="_blank"} for further details

    **Returns** :

    * The current date-time as a `ZonedDateTime`

    **Examples** :

    SpEL without : `${T(java.time.ZonedDateTime).now()}`

    SpEL with    : `${#now()}`

## timeAmount

!!! note "TemporalAmount timeAmount(String text)"

    Create a TemporalAmount from a given string.  
    This is usefull when combine with other methods or functions.

    See [timeAmount(TemporalAmount)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/TemporalAmount.html){:target="_blank"} for further details

    **Returns** :
    
    * A `TemporalAmount`

    **Examples** :

    SpEL without : `${#now().plus(T(java.time.Duration).parse("6 hours"))}`

    SpEL with    : `${#now().plus(#timeAmount("6 hours"))}`

## timeUnit

!!! note "ChronoUnit timeUnit(String unit)"

    See [timeUnit(ChronoUnit)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/temporal/ChronoUnit.html){:target="_blank"} for further details

    **Parameters** :

    * Possible values are :
        * "nanos", "ns"
        * "micros", "µs"
        * "millis", "ms"
        * "seconds", "s", "sec"
        * "minutes", "m", "min"
        * "hours", "h", "hour", "hours", "hour(s)"
        * "days", "d", "day", "days", "day(s)"
        * "weeks"
        * "months"
        * "years"
        * "decades"
        * "centuries"
        * "millennia"
        * "eras"
        * "forever"

    **Returns** :
    
    * A `ChronoUnit`

    **Examples** :

    SpEL without : `${T(java.time.temporal.ChronoUnit).valueOf("hours".toUpperCase())}`

    SpEL with    : `${#timeUnit("h")}`
