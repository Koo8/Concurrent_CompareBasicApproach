package Executor_Scheduled;

import java.util.Date;

public class TaskToSchedule implements Runnable {

    private String name;
    public TaskToSchedule (String name ){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " to start at "+ new Date());
    }
}
