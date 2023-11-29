package com.yk.Motivation.standard.util;// DateUtils.java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

    public static String timeForToday(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (days < 1) {
            long minutes = ChronoUnit.MINUTES.between(dateTime, now);
            if (minutes < 1) return "방금전";
            if (minutes < 60) return minutes + "분전";

            long hours = ChronoUnit.HOURS.between(dateTime, now);
            if (hours < 24) return hours + "시간전";

            return "오늘";
        } else if (days < 7) {
            return days + "일전";
        } else {
            return dateTime.format(formatter);
        }
    }
}
