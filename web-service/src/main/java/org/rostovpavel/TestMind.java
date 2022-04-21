package org.rostovpavel;

import org.rostovpavel.utils.DateTimeConfig;
import org.rostovpavel.utils.DateTimeFormatter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class TestMind {
    public static void main(String[] args) {
        var monday = DateTimeConfig.MONDAY.getValue();
        System.out.println("monday = " + Arrays.toString(monday));
        for (int i = 0; i < monday.length; i =i +2) {
            System.out.println("i = " + i);
        }

        Instant currentNow = Instant.now();
        DayOfWeek dayOfWeek = Instant.now().atZone(ZoneId.systemDefault()).getDayOfWeek();


        int[] currentDateConfig = DateTimeFormatter.getCurrentDateConfig();


        Instant now = Instant.now();
        Instant minus = currentNow.minus(3, ChronoUnit.DAYS);

        System.out.printf("");
    }
}
