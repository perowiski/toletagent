package services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by seyi on 7/11/15.
 */
public class Exec {
    public static ExecutorService Ex = Executors.newCachedThreadPool();

    public static ScheduledExecutorService ScEx = Executors.newScheduledThreadPool(20);
}