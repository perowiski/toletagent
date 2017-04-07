package utility;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Seconds;

/**
 * Created by seyi on 11/4/14.
 */
public class CronUtil {
    public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    public static int nextExecutionInHours(int hour, int minute){
        return Hours.hoursBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getHours();
    }

    public static DateTime nextExecution(int hour, int minute){
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }
}
