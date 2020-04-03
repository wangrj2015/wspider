package com.rjay.wspider.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateUtil {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static Timestamp strToSqlDate(String dataTime) {
        LocalDateTime date = LocalDateTime.parse(dataTime, dtf);
        return Timestamp.valueOf(date);
    }

    public static String nowDateStr() {
        LocalDateTime date = LocalDateTime.now();
        return dtf.format(date);
    }
}
