package org.rostovpavel.webservice.utils;


import com.google.protobuf.Timestamp;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static ru.tinkoff.piapi.core.utils.DateUtils.*;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Log4j2
public class DateFormatter {

    public static int[] getCurrentDateConfig() {
        DayOfWeek currentDayOfWeek = Instant.now().atZone(ZoneId.systemDefault()).getDayOfWeek();
        //System.out.println("currentDayOfWeek = " + currentDayOfWeek);
        int[] values;
        switch (currentDayOfWeek) {
            case MONDAY -> values = DateTimeConfig.MONDAY.getValue();
            case TUESDAY -> values = DateTimeConfig.TUESDAY.getValue();
            case WEDNESDAY -> values = DateTimeConfig.WEDNESDAY.getValue();
            case THURSDAY -> values = DateTimeConfig.THURSDAY.getValue();
            case FRIDAY -> values = DateTimeConfig.FRIDAY.getValue();
            case SATURDAY -> values = DateTimeConfig.SATURDAY.getValue();
            case SUNDAY -> values = DateTimeConfig.SUNDAY.getValue();
            default -> throw new IllegalStateException("Unexpected value:" + currentDayOfWeek + " when get current day of week");
        }
        return values;
    }

    public static String getTimeToString(String date){
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"));
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

    private static void printCandleToLog(HistoricCandle candle) {
        var open = quotationToBigDecimal(candle.getOpen());
        var close = quotationToBigDecimal(candle.getClose());
        var high = quotationToBigDecimal(candle.getHigh());
        var low = quotationToBigDecimal(candle.getLow());
        var volume = candle.getVolume();
        var time = timestampToString(candle.getTime());
        log.info(
                "цена открытия: {}, цена закрытия: {}, минимальная цена за 1 лот: {}, максимальная цена за 1 лот: {}, объем " +
                        "торгов в лотах: {}, время свечи: {}",
                open, close, low, high, volume, time);
    }

}
