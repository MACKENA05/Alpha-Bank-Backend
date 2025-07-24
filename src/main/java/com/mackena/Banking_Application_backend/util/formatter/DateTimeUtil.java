package com.mackena.Banking_Application_backend.util.formatter;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        if (days < 30) return days + " day" + (days == 1 ? "" : "s") + " ago";

        return dateTime.format(DATE_FORMAT);
    }

    public String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMAT) : "";
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    public LocalDateTime getStartOfMonth() {
        return LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public LocalDateTime getStartOfDay() {
        return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}


