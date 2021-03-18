package Executor_Submit_Callable_Future;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * executor.submit takes in callable as parameter and return Future<parameter>,
 * future has isDone(), get() methods
 */

public class FC_Main {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Random random = new Random();

        for (int i = 0; i <10 ; i++) {
            int rand = random.nextInt(10);
            FactorialCalculator factorialCalculator = new FactorialCalculator(rand);
            Future<Integer>  result = executor.submit(factorialCalculator);
            try {
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
