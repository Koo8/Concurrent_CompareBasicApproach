package Executor_InvokeAll;

import java.util.Random;
import java.util.concurrent.Callable;

public class TaskToCreateResult implements Callable<Result> {
    private String name;
    public TaskToCreateResult(String name){
        this.name = name;
    }

    @Override
    public Result call() throws Exception {
        long startTime = System.currentTimeMillis();
        Random random = new Random();
        Result result = new Result();
        int value = 0;
        try{
            long duration = (long) (Math.random()*10);

            // create a value for Result class
            for (int i = 0; i <5 ; i++) {
               value += random.nextInt(100);
            }
            result.setName(this.name);
            result.setValue(value);
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + " -> " + this.name + " used " + (endTime -startTime ) + " to finish creating the result with its value " + result.getValue());
        return result;
    }
}
