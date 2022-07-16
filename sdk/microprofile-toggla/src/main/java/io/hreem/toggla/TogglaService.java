package io.hreem.toggla;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import jakarta.ws.rs.ForbiddenException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class TogglaService {

    private final static Logger Log = Logger.getLogger(TogglaService.class.getName());

    @Inject
    @ConfigProperty(name = "toggla.auto-toggle-refresh-rate.cron", defaultValue = "0/30 * * 1/1 * ? *")
    String togglePollSchedule;

    @Inject
    ToggleStore toggleStore;

    @Inject
    CDIJobFactory jobFactory;

    @Inject
    @RestClient
    TogglaGateway togglaGateway;

    @Inject
    Utils utils;

    @PostConstruct
    void init() {
        try {
            schedulePoller();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules the poller to run every 30 minutes (default), or according to the
     * cron schedule provided by the user.
     * 
     * @throws SchedulerException
     */
    void schedulePoller() throws SchedulerException {
        final var sf = new StdSchedulerFactory();
        final var sched = sf.getScheduler();
        sched.setJobFactory(jobFactory);

        final var job = newJob(TogglaPollingJob.class)
                .withIdentity("TogglePoller")
                .build();

        // computer a time that is on the next round minute
        final var runSchedule = CronScheduleBuilder.cronSchedule(togglePollSchedule);
        final var trigger = newTrigger()
                .withIdentity("TogglePollerTrigger")
                .withSchedule(runSchedule)
                .forJob(job)
                .build();

        sched.scheduleJob(job, trigger);
        sched.start();
    }

    Boolean fetch(String key, String variationKey) {
        final var toggleKey = utils.constructKey(key, variationKey);
        var currentStatus = toggleStore.getToggle(toggleKey);
        if (currentStatus == null) {
            boolean status = false;
            try {
                if (variationKey != null) {
                    status = togglaGateway.getToggleVariationStatus(key, variationKey);
                } else {
                    status = togglaGateway.getToggleStatus(key);
                }
                toggleStore.putToggle(toggleKey, status);
                Log.log(Level.FINE, "Polled toggle " + toggleKey + " for the first time with status " +
                        status);
                return status;
            } catch (ForbiddenException e) {
                return utils.handleToggleCallException("Incorrect API Key provided, cannot invoke Toggla API",
                        toggleKey, e);
            } catch (Exception e) {
                return utils.handleToggleCallException(
                        "Failed to fetch toggle from Toggla API, falling back to 'false'",
                        toggleKey, e);
            }
        }
        return currentStatus;

    }

}
