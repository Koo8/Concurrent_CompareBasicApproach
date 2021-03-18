package Executor_InvokeAll;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class InvokeAll_Main {

    public static void main(String[] args) {
        // executor use invokeAll to process all tasks
        ExecutorService executor = Executors.newCachedThreadPool();
        // invokeAll needs Callable list as parameter, Future list as return type
        List<Callable<Result>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
          TaskToCreateResult task = new TaskToCreateResult("T-" + i);
          tasks.add(task);
        }
        List<Future<Result>> futureList = new ArrayList<>();
        try {
           futureList = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < futureList.size(); i++) {
            Future<Result> resultInFuture = futureList.get(i);
            try {
                Result result = resultInFuture.get();
                System.out.println(result.getName() + ": " + result.getValue());
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
