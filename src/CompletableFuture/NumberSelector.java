package CompletableFuture;

import java.util.List;
import java.util.function.Function;

/**
 * This class takes a List<long> as parameter, return a long value that is
 * the average of max and min, This will be used by a completableFuture as
 * a function parameter
 */

public class NumberSelector implements Function<List<Long>, Long> {

    @Override
    public Long apply(List<Long> list) {
        System.out.println("Step3: Start");
        long max = list.stream().max(Long::compare).get();  // max is the special case of reduction
        long min = list.stream().min(Long::compare).get();
        long average = (max + min) /2;
        System.out.println("Step3: average is "+ average + " -> " + Thread.currentThread().getName());
        return average;
    }
}
