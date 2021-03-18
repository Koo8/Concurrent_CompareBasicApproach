package CompletableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NumberListGenerator implements Supplier<List<Long>> {
    // this class needs to use the integer that is wrapped inside a completableFuture by SeedGenerator
    int seed;
    NumberListGenerator(int seed){
        this.seed = seed;
    }

    @Override
    public List<Long> get() {
        System.out.printf("%s -> NumberListGenerator: Starts. \n", Thread.currentThread().getName());
        List<Long> list = new ArrayList<>(); // create an empty list
        for (int i = 0; i < seed * 1000; i++) {
           long number = Math.round(Math.random()*1000000); // Math.round() convert double to long, similar to Math.rint
           list.add(number);
        }
        System.out.println("NumberListGenerator is finished. The list is created");
        return list;
    }
}
