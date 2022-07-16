package io.hreem.toggla;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

@ApplicationScoped
public class CDIJobFactory implements JobFactory {

    @Inject
    BeanManager beanManager;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        final var jobClazz = bundle.getJobDetail().getJobClass();
        final var bean = beanManager.getBeans(jobClazz).iterator().next();
        final var context = beanManager.createCreationalContext(bean);
        return (Job) beanManager.getReference(bean, jobClazz, context);
    }

}
