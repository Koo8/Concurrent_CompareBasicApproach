package Executor_InvokeAny;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * So, we have two tasks that can return the true value or throw Exception. You can have
 * the following four possibilities:
 * Both tasks return the true value. Here, the result of the invokeAny() method is
 * the name of the task that finishes in the first place.
 * The first task returns the true value and the second one throws Exception.
 * Here, the result of the invokeAny() method is the name of the first task.
 * The first task throws Exception and the second one returns the true value.
 * Here, the result of the invokeAny() method is the name of the second task.
 * Both tasks throw Exception. In such a class, the invokeAny() method throws
 * an ExecutionException exception.
 */

public class InvokeAny_Main {

    public static void main(String[] args) {
        // Use ThreadPoolExecutor to do the tasks
        ExecutorService executor = Executors.newCachedThreadPool();
        String userName = "test";
        String userPassword = "testPassWord";
        UserValidator v1 = new UserValidator("DSPT");
        UserValidator v2 = new UserValidator("DataBase");

        // create a task list that can be use by invokeAny()
        List<ValidateTask> list = new ArrayList<>();
        ValidateTask t1 = new ValidateTask(v1, userName, userPassword);
        ValidateTask t2 = new ValidateTask(v2, userName, userPassword);
        list.add(t1);
        list.add(t2);

        // use invokeAny() to start all tasks
        try {
            executor.invokeAny(list);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("Validation is finished.");

    }
}
