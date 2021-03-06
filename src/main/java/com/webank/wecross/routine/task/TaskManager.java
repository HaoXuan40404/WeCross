package com.webank.wecross.routine.task;

import java.util.List;
import java.util.Properties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager {

    private Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private Scheduler scheduler;
    private TaskFactory taskFactory;

    public TaskManager(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
        try {
            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            Properties props = new Properties();
            props.put(
                    StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                    "org.quartz.simpl.SimpleThreadPool");
            props.put("org.quartz.threadPool.threadCount", "1");
            stdSchedulerFactory.initialize(props);
            scheduler = stdSchedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            logger.error("something wrong with getting scheduler: {}", e.getLocalizedMessage());
        }
    }

    public void registerTasks(Object... args) {
        List<Task> tasks = taskFactory.load(args);
        try {
            for (Task task : tasks) {
                scheduler.scheduleJob(task.getJobDetail(), task.getTrigger());
            }
        } catch (SchedulerException e) {
            logger.error("something wrong with registering tasks: {}", e.getLocalizedMessage());
        }
    }

    public void start() throws SchedulerException {
        logger.info("scheduler starts working");
        scheduler.start();
    }
}
