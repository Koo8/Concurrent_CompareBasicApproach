package Executor_Scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Schedule_Main {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
         // use schedule() to schedule tasks
        //        for (int i = 0; i <5; i++) {
//           TaskToSchedule task = new TaskToSchedule("T"+i);
//           executor.schedule(task,i+1, TimeUnit.SECONDS);
//        }
        // use scheduleAtFixedRate() to schedule tasks to repeat continously till being canceled.
        TaskToSchedule theTask = new TaskToSchedule("scheduled task");
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(theTask, 1,1, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            System.out.println("Time before starting  " + future.getDelay(TimeUnit.MILLISECONDS));
            Thread.sleep(500);
        }
        Thread.sleep(2500);
        executor.shutdown();
    }
}
