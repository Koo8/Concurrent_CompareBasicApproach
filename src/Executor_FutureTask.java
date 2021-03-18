import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Use a futureTask as a runnable for an executor
 * The futureTask has two types of constructors - callable, or runnable + result.
 * futureTask done() is called internally when into isDone() state including by cancelling
 */

public class Executor_FutureTask {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        // run 5 FutureTasks as runnable - the futureTask in this program wrap a callable
        List<ResultFutureTask> tasks = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            ExecuteCallables executeCallable = new ExecuteCallables("Task--"+i);
            ResultFutureTask resultFutureTask = new ResultFutureTask(executeCallable);
            tasks.add(resultFutureTask);
            executor.submit(resultFutureTask);
        }

        // sleep for 2 seconds
        Thread.sleep(2000);

        // cancel tasks
        for(ResultFutureTask task: tasks){
            task.cancel(true);
        }
        // print out result if not cancelled
        for (ResultFutureTask task: tasks){
            if(!task.isCancelled()) {
                try {
                    String str = task.get();
                    System.out.println("Task of "+ task.getName()+ " have a result of "+str);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(task.getName() + " has been cancelled." );
            }
        }

        executor.shutdown();

    }

}

class ExecuteCallables implements Callable<String> {

    private String name;
    ExecuteCallables(String name ){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String call() throws Exception {
        long duration = (long)(Math.random()*10);   // () around Math.random() * 10 is a must, otherwise, it is always a 0.
        System.out.println(name + " has a sleep for "+ duration);
        TimeUnit.SECONDS.sleep(duration);
        String result= "Hello World " + " in task "+ name;
        return result;
    }
}

class ResultFutureTask extends FutureTask<String> {
    private String name;

    public ResultFutureTask(ExecuteCallables callable) {
        super(callable);
        name = callable.getName();
    }

    @Override
    protected void done() { // may be cancelled or finished, into isDone() state
        if(isCancelled()) {
            System.out.println(name + " is cancelled. --ResultFutureTask");
        }else {
            System.out.println(name + " is finished. -- ResultFutureTask ---> "+ isDone() + " for isDone()" );

        }
    }

    public String getName() {
        return name;
    }
}
