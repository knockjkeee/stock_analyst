package org.rostovpavel.webservice.utils;


import com.google.protobuf.Timestamp;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static ru.tinkoff.piapi.core.utils.DateUtils.*;

public class DateTimeFormatter {

    public static int[] getCurrentDateConfig() {
        DayOfWeek currentDayOfWeek = Instant.now().atZone(ZoneId.systemDefault()).getDayOfWeek();
        System.out.println("currentDayOfWeek = " + currentDayOfWeek);
        int[] values;
        switch (currentDayOfWeek) {
            case MONDAY -> values = DateTimeConfig.MONDAY.getValue();
            case TUESDAY -> values = DateTimeConfig.TUESDAY.getValue();
            case WEDNESDAY -> values = DateTimeConfig.WEDNESDAY.getValue();
            case THURSDAY -> values = DateTimeConfig.THURSDAY.getValue();
            case FRIDAY -> values = DateTimeConfig.FRIDAY.getValue();
            default -> throw new IllegalStateException("Unexpected value:" + currentDayOfWeek + " when get current day of week");
        }
        return values;
    }


    public static Instant getCurrentInstantAtTimeZone() {
        int totalSecondsInstantNow = Instant.now().atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds();
        return Instant.now().plus(totalSecondsInstantNow, ChronoUnit.SECONDS);
    }

    public static Instant getCurrentInstantAtTimeZone(@NotNull Instant instant) {
        int totalSecondsInstantNow = instant.atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds();
        return instant.plus(totalSecondsInstantNow, ChronoUnit.SECONDS);
    }

    public static String getTimeStampToStringAtCurrentTimeZone(@NotNull Timestamp timestamp) {
        Instant instant = timestampToInstant(timestamp);
        Instant currentInstantAtTimeZone = getCurrentInstantAtTimeZone(instant);
        Timestamp resultTimestamp = instantToTimestamp(currentInstantAtTimeZone);
        return timestampToString(resultTimestamp);
    }


}
