package ForkJoin;

import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

/**
 * This is from Oracle doc sample program   https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CountedCompleter.html
 * CC is good for subtasks stall or blockage situations, because CountedCompleters provide their own continuations,
 * other threads need not block waiting to perform them.
 * Pending tasks starts at 0 by default, but can be set to any number by
 * setPendingCount(), can also be changed by addToPendingCount(), compareAndSetPendingCount();
 * <p>
 * A concrete subclass of CC needs to override compute() and invoke tryComplete() once before returning.
 * Normally CC don't bear results, use CountedCompleter<Void>
 * that return null. If needs to return a result, override getRawResult() to
 */

public class CountedCompleter_ParallelRecursiveDecompsition {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        MyOperation<Integer> op = new MyOperation<Integer>();
        List list;
        Integer[] array = new Integer[4];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        ForEach<Integer> cc = new ForEach<Integer>(null, array, op, 0, array.length);
        pool.invoke(cc);    // TODO: test out
    }
}

class MyOperation<E> {
    void apply(E e) {
        System.out.println("MyOperation : " + e.toString() + " is being applied");
    }
}

class ForEach<E> extends CountedCompleter<Void> {
    // static utility method setup the base task and invoke it using ForkJoinPool.commonPool();
//    public static <E> void forEach(E[] array, MyOperation<E> op) {
//        System.out.println("in static foreach()");
//        new ForEach<E>(null, array, op, 0, array.length).invoke();
//    }

    final E[] array;
    final MyOperation<E> op;
    int lo, hi;    // NOTE: can't be final, otherwise hi=mid can't be assigned later

    ForEach(CountedCompleter<?> p, E[] array, MyOperation<E> op, int lo, int hi) {
        super(p);    // p is parent CC -> super() create a new CC with the parent CC as parameter
        this.array = array;
        this.op = op;
        this.lo = lo;
        this.hi = hi;
    }

//    public void compute() { // version 1 and version 2;
//
//        System.out.println(getCompleter() + " hi is " + hi);
//
//        if (hi - lo >= 2) {  // hi is the length of array, so it should be always bigger than lo
//            int mid = (lo + hi) >>> 1;
//            // must!
//           // setPendingCount(1); // must set pending count before fork
//            // instead of using setPendingCount(), add one count for each fork(); The left child is not really needed if move hi to mid position
//            addToPendingCount(1);
//            new ForEach<E>(this, array, op, mid, hi).fork(); // right child. this keyword implies this CC will be the parent CC for the new ForEach class
//            new ForEach<E>(this, array, op, lo, mid).compute(); //directly invoke left child // or .fork() instead
//
//        } else {
//            if (hi > lo) // when array only has one number, do the op.apply()
//                op.apply(array[lo]);
//           // tryComplete();
//            // since this program doesn't call onCompletion(); use this instead
//            propagateCompletion();
//        }                      // TODO??? change setPendingCount to 1, 2 or 3, the results are almost the same
//                               // TODO??? tryComplete() inside else{} or outside else{} but inside compute{} -> output are not much different.
//    }                          // fork() or compute() -> output are not much different


    @Override
    public void compute() {
        // since hi and lo will be updated in the while loop
        int l = lo, h= hi;
        while(h - l >=2){
            int mid = (h + l) >>>1;
            addToPendingCount(1);
            new ForEach(this,array,op, mid, h).fork();
            h = mid;
        }
        if(h >l) {
            op.apply(array[l]);
        }
        propagateCompletion();
    }
}
