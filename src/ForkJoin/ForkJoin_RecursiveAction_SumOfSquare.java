package ForkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * NOTE: RecursiveActions need not be fully recursive, so long as they maintain
 * the basic divide-and-conquer approach.
 * This program sums the squares of each element of a double array, by
 * subdividing out only the right-hand-sides of repeated divisions by two,
 * and keeping track of them with a chain of nextRightHalf references.
 * It uses a dynamic threshold based on method getSurplusQueuedTaskCount,
 * but (I commented out this part) counterbalances potential excess partitioning
 * by directly performing leaf actions on unstolen tasks rather than further subdividing.
 * This is an example of oracle
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/RecursiveAction.html
 */

/**
 * NOTE: To avoid too big framework overhead, if many jobs are being submitted to the same pool, ForkJoinTask::getSurplusQueuedTaskCount
 * can be used to dynamically decide whether to spawn sub-tasks.
 */
public class ForkJoin_RecursiveAction_SumOfSquare {

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        // create a ramdom array of double
        double[] array = new double[50];
        for (int i = 0; i < array.length; i++) {
            array[i] = 2;
        }
        double result = sumOfSquares(pool, array);
        System.out.println(result);
    }

    private static double sumOfSquares(ForkJoinPool pool, double[] array) {
        int n = array.length;
        Applyer a = new Applyer(array, 0, n, null);
        // method 1 - submit(), must use join() for final result
//        ForkJoinTask task =  pool.submit(a);// submit() won't get result
//        task.join();
        // method 2 - execute() not working here TODO: not sure how to handle this
        //pool.execute(a);
        // method 3 - invoke()
        pool.invoke(a);  // invoke equals submit() + join()
        return a.result;
    }
}

class Applyer extends RecursiveAction {
    private final double[] array;
    private final int lo, hi;
    double result;   // since recursiveAction is a resultless forkJoinTask, this "result" variable is for carrying the result of the action
    private Applyer nextRightHalf; // keeps track of right-hand-side tasks, when starting, There are not spliting yet, so there are null rightArray yet

    Applyer(double[] array, int lo, int hi, Applyer nextRightHalf) {
        this.array = array;
        this.lo = lo;
        this.hi = hi;
        this.nextRightHalf = nextRightHalf;
    }

//    private double addToSum(int l, int h) {
//        double sum = 0;
//        for (int i = l; i < h; ++i) // perform leftmost base step
//            sum += array[i] * array[i];
//        return sum;
//    }

    protected void compute() {
        // set new variables so that they can be updated inside the while loop
        int l = lo;
        int h = hi;
        Applyer rightHalf = null; // to register itself, so when while(rightHalf != null) loop, keeps on refer to a previous half array
        // keep on break the array in to halves, till it is short enough (less than 10)
        while (h - l > 10 /*&& getSurplusQueuedTaskCount() <= 3*/) { // highlight: see top note
            int mid = (l + h) >>> 1;    // first time(0,50) -> m is 25, 12, 6, 3 sequentially before go out of the while loop; second time(25, 50) m is 37, 31, 28,26, then there are (12, 25)18,15,9 and (6,12)
            rightHalf = new Applyer(array, mid, h, rightHalf);
            rightHalf.fork();
            h = mid; // keep on cutting in half till the array is smaller than 10 numbers
        }
        // calculate the revised l to h portion of array - the LEFT part of the array
     //   System.out.println("l is "+ l + " h is "+ h);

        double sum = 0; //addToSum(l, h);
        for (int i = l; i < h; ++i) // perform leftmost base step
            sum += array[i] * array[i];  //
        // calculate the RIGHT part of the array
        while (rightHalf != null) {  // when there are still rightHalf array existed
//            if (rightHalf.tryUnfork()) // directly calculate if not stolen
//            {
//                sum += rightHalf.addToSum(rightHalf.lo, rightHalf.hi);
//                System.out.println(" ^^ inside tryUnfork() block , right applyer is calculated");
//            } else {
                rightHalf.join();    // if it is forked to other thread, join() to get the result
                sum += rightHalf.result;  // add on the right half array result
//            }
            rightHalf = rightHalf.nextRightHalf;
        }
        // use the variable result to carry the sum value to this class instance.
        result = sum;
    }
}
