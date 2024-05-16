package com.example.app_2100.helper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
public class DateFormatter {

    public static String formatDate(Date date) {
        long currTimeMillis = System.currentTimeMillis();
        long timeDiffMillis = currTimeMillis - date.getTime();

        long seconds = timeDiffMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = weeks / 4;

        if (minutes < 60) {
            return formatTimeUnit(minutes, "minute");
        } else if (hours < 24) {
            return formatTimeUnit(hours, "hour");
        } else if (days < 7) {
            return formatTimeUnit(days, "day");
        } else if (weeks < 4) {
            return formatTimeUnit(weeks, "week");
        } else if (months < 12) {
            return formatTimeUnit(months, "month");
        } else {
            return formatDateFull(date);
        }
    }

    private static String formatTimeUnit(long value, String unit) {
        return value + " " + unit + (value == 1 ? "" : "s") + " ago";
    }

    private static String formatDateFull(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        int year = localDate.getYear() + 1900; // note that the year starts from 1900 in 'Date' class

        return day + " " + months[month] + " " + year;
    }
}
