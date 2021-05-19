package cn.xhalo.blog.auth.server.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public final static String dateToLocalDateTime(Date date,String pattern) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public final static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public final static long getDiffMinutes(Date startDate,Date endDate) {
        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        return Duration.between(startInstant,endInstant).toMinutes();
    }


}
