package services;

import controllers.*;
import org.apache.commons.lang3.time.DateUtils;
import play.Environment;
import play.Mode;
import search.Searcher;
import utility.CronUtil;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by seyi on 7/25/16.
 */
@Singleton
public class Starter {
    @Inject
    public Starter(Environment environment) {
        Exec.Ex.execute(() -> {
        });
        if (environment.mode() == Mode.DEV) {
            String host = null;
            String dbUrl = System.getenv("DATABASE_URL");
            if (Utility.isNotEmpty(dbUrl)) {
                host = dbUrl.split("//")[1].split(":")[0];
            }
            Searcher.start(host);
        } else {
            Searcher.start("localhost");

            Exec.Ex.execute(() -> {
                //ApiSave.crawlLocation();
            });

            Exec.ScEx.scheduleAtFixedRate(() -> {
                ApiSave.crawlAgents(null, null);
            }, CronUtil.nextExecutionInHours(5, 0), 24, TimeUnit.HOURS);

            Exec.ScEx.scheduleAtFixedRate(() -> {
                ApiSave.fetchAgentDatas();
            }, CronUtil.nextExecutionInHours(6, 0), 24, TimeUnit.HOURS);


            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");
            Date currentTime = new Date();
            Date currenTimeMinus24 = DateUtils.addHours(currentTime, -24);
            String start = format.format(currenTimeMinus24);
            String end = format.format(currentTime);
            Exec.ScEx.scheduleAtFixedRate(() -> {
                ApiSave.crawlAgentLeads(start, end);
            }, CronUtil.nextExecutionInHours(1, 0), 24, TimeUnit.HOURS);

            Exec.ScEx.scheduleAtFixedRate(() -> {
                ApiSave.crawlPropertyLeads(start, end);
            }, CronUtil.nextExecutionInHours(3, 0), 24, TimeUnit.HOURS);

            Exec.ScEx.scheduleAtFixedRate(() -> {
                BackgroundServices.inspectionNotifier();
            }, 0, 5, TimeUnit.MINUTES);
        }
    }

    @PreDestroy
    public void destroy() {
        Searcher.stop();
    }
}