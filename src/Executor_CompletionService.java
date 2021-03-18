import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ExecutorCompletionService returns futures objects oo based on completion order,
 * this saves a lot of waiting time consumed by normal executor get();
 * so whichever task executes first, will be returned first. You just
 * need to call executorCompletionService.take() to get completed Future object.
 * Otherwise, use normal executor for 5 tasks, and task1.get() will block all other
 * tasks to hand out their results.
 * "All tasks started simultaneously but We retrieved the results based on the
 * completion order with the help of executorCompletionService.take().get()
 * rather than calling get() method on future object.
 *
 *
 * T-4 is starting to multiply...
 * T-3 is starting to multiply...
 * T-2 is starting to multiply...
 * T-1 is starting to multiply...
 * T-4 has completed.
 * 1200 T-3 has completed.
 * 840 T-2 has completed.
 * 1000 T-1 has completed.
 * 200
 * OO Method with completionService takes 4077 milliseconds
 * T-1 is starting to multiply...
 * T-1 has completed.
 * T-2 is starting to multiply...
 * T-2 has completed.
 * T-3 is starting to multiply...
 * T-3 has completed.
 * T-4 is starting to multiply...
 * T-4 has completed.
 * [200, 1000, 840, 1200]
 * OO Method with Executor takes 10027 milliseconds
 */

public class Executor_CompletionService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create a completionService
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        // create 4 tasks
        long startt1 = System.currentTimeMillis();
        MultiplyTask t1 = new MultiplyTask("T-1" ,20, 10, 4000);
        MultiplyTask t2 = new MultiplyTask("T-2" ,200, 5, 3000);
        MultiplyTask t3 = new MultiplyTask("T-3" ,120, 7, 2000);
        MultiplyTask t4 = new MultiplyTask("T-4" ,30, 40, 1000);

        List<Future<Integer>>  resultFutures = new ArrayList<>();
        resultFutures.add(completionService.submit(t4));
        resultFutures.add(completionService.submit(t1));
        resultFutures.add(completionService.submit(t2));
        resultFutures.add(completionService.submit(t3));

        for (int i = 0; i <resultFutures.size() ; i++) {
            try {
                int result = completionService.take().get();    // take() -> Retrieves and removes the Future representing the next completed task, waiting if none are yet present.
                System.out.print(result + " ");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        long endT1 = System.currentTimeMillis();
        System.out.println("Method with completionService takes " + (endT1-startt1) +  " milliseconds");

        long startt2 = System.currentTimeMillis();
        MultiplyTask t5 = new MultiplyTask("T-1" ,20, 10, 4000);
        MultiplyTask t6 = new MultiplyTask("T-2" ,200, 5, 3000);
        MultiplyTask t7 = new MultiplyTask("T-3" ,120, 7, 2000);
        MultiplyTask t8 = new MultiplyTask("T-4" ,30, 40, 1000);

        List<Integer> answers = new ArrayList<>();
        answers.add(completionService.submit(t5).get());
        answers.add(completionService.submit(t6).get());
        answers.add(completionService.submit(t7).get());
        answers.add(completionService.submit(t8).get());
        System.out.println(answers);
        long endt2 = System.currentTimeMillis();
        System.out.println("Method with Executor takes "+ (endt2 - startt2) + " milliseconds");

        executor.shutdown();


    }
}

class MultiplyTask implements Callable<Integer>{
    private int num1, num2;
    private long sleepTime;
    private String name;

    public MultiplyTask(String name,int n1, int n2, long sleepTime) {
        num1 = n1;
        num2 = n2;
        this.name = name;
        this.sleepTime = sleepTime;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println(name + " is starting to multiply...");
        int result =num1 *num2;
        TimeUnit.MILLISECONDS.sleep(sleepTime);
        System.out.println(name + " has completed.");
        return result;
    }
}