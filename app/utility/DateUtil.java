package utility;

import org.apache.commons.lang3.time.DateUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static long remainingTime(Date date, int duration) {
        if(date == null) return 0;
        LocalDateTime endDate = LocalDateTime.ofInstant(DateUtils.addHours(date, duration).toInstant(), ZoneId.systemDefault());
        LocalDateTime currentDate = LocalDateTime.now();
        long numberOfHours = Duration.between(currentDate, endDate).toHours();
        if(numberOfHours < 0)
            return 0;
        return numberOfHours;
    }

}