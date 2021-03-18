package CompletableFuture;

import java.util.concurrent.CompletableFuture;

public class SeedGenerator implements Runnable {
    // need this cf so that to use complete() later to create a CompletableFuture that contains data
    CompletableFuture<Integer> cf;
    SeedGenerator(CompletableFuture<Integer> cf){
        this.cf = cf;
    }

    @Override
    public void run() {
        System.out.println("SeedGenerator: Start to generate the seed " + Thread.currentThread().getName());
        // wait for 500 milliseconds
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // generate the seed number
        int seed= (int) Math.rint(Math.random() * 10);  // Math.rint is similar to Math.round, except it takes double as param and return double of an Integer
        //highlight: use complete() to wrap any data of the correct type into the completableFuture
        cf.complete(seed); // now the future is containing this seed
        System.out.println("SeedGenerator: seed " + seed  + " is generated.");
    }
}
