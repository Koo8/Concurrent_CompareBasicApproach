package ForkJoin;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicReference;

/** To search data for a target object, then stop search further
 * Oracle CC class doc example #2 https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CountedCompleter.html
 * introduce quietlyCompleteRoot() of CC, AtomicReference.compareAndSet()
 */
public class CountedCompleter_SearchArray {
}

class Searcher<E> extends CountedCompleter<E> {
    final E[] array;
    final AtomicReference<E> result;
    final int lo, hi;

    Searcher(CountedCompleter<?> p, E[] array, AtomicReference<E> result, int lo, int hi) {
        super(p);
        this.array = array;
        this.result = result;
        this.lo = lo;
        this.hi = hi;
    }

    public E getRawResult() {
        return result.get();
    }

    public void compute() { // similar to ForEach version 3
        int l = lo, h = hi;
        while (result.get() == null && h >= l) {
            if (h - l >= 2) {
                int mid = (l + h) >>> 1;
                addToPendingCount(1); // add one pending task count when you start a task       // match the one fork();
                new Searcher<E>(this, array, result, mid, h).fork();
                h = mid;
            } else {
                E x = array[l];
                if (matches(x) && result.compareAndSet(null, x))
                    quietlyCompleteRoot(); // root task is now joinable
                break;
            }
        }
        tryComplete(); // normally complete whether or not found
    }

    private boolean matches(E e) {
        return Integer.parseInt(e.toString())>Math.random()*5;
    } // return true if found

    public static <E> E search(E[] array) {
        return new Searcher<E>(null, array, new AtomicReference<E>(), 0, array.length).invoke();
    }
}
