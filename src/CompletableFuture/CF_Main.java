package CompletableFuture;

import java.util.List;                                   // highlight: p175
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * There are 3 tasks to be completed
 * 1. Step 1 will calculate the nearest number to 1,000, in a list of random numbers.
 * 2. Step 2 will calculate the biggest number in a list of random numbers.
 * 3. Step 3 will calculate the average number between the largest and smallest
 * numbers in a list of random numbers.
 */

public class CF_Main {
    public static void main(String[] args) {
        // first create a seed integer  - SeedGenerator
        CompletableFuture<Integer> seedFuture = new CompletableFuture();
        SeedGenerator seedGenerator = new SeedGenerator(seedFuture);
        Thread seedThread = new Thread(seedGenerator);
        seedThread.start();// highlight: use completableFuture as a runnable, this task can be accomplished through thread.
        // second create a list of long numbers using seed integer  - NumberListGenerator
        int seedInt = 0;
        try {
            seedInt = seedFuture.get();
            System.out.println("Seed is " + seedInt);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        NumberListGenerator numberListGenerator = new NumberListGenerator(seedInt);
        CompletableFuture<List<Long>> listFuture = CompletableFuture.supplyAsync(numberListGenerator);
     //   listFuture.join(); // highlight: join() for completableFuture is the same as start() for thread.


        // use the list generated to complete the 3 tasks listed above using CompletableFuture
        // NOTE: completableFuture can chain async tasks in different stages
        CompletableFuture<Long> task1 = listFuture.thenApplyAsync(list-> {  // listFuture contains the "list". thenApplyAsync return a CompletableFuture
            System.out.println("task1 nearest to 1000 is starting.");
            long selected = 0;
            long distance = 0;
            long selectedDistance = Long.MAX_VALUE;
            for(Long num: list){
               distance = Math.abs(num-1000);// the absolute distance to 1000;
                if(distance < selectedDistance){
                    selectedDistance = distance;
                    selected = num;
                }
            }
            System.out.println("task1 nearest to 1000 is "+ selected + " -> "+ Thread.currentThread().getName());
            return selected;// highlight: ??? why should return completablefuture, but long is accepted.
        });

        CompletableFuture<Void> task2 = listFuture.thenApplyAsync(list -> {
            System.out.println("Task2 starting...");
//            long biggest =
//            for(long num : list){
//                if (num > biggest){
//                    biggest = num;
//                }
//            }
          long biggest =  list.stream().max(Long::compare).get();
          return biggest;

        }).thenAccept((param)->
                System.out.println( "Task2 the biggest is "+ param + " -> "+ Thread.currentThread().getName()));


        NumberSelector numberSelector = new NumberSelector();
        System.out.println("Task3 starts...");
        CompletableFuture<Long> task3 = listFuture.thenApplyAsync(numberSelector);
        // finalize
        // allOf() return a new future after all futures included are completed.
        CompletableFuture<Void> runAllTasks = CompletableFuture.allOf(task1, task2, task3);
        runAllTasks.join();  //
    }
}
