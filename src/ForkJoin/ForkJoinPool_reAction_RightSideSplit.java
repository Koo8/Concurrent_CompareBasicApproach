package ForkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPool_reAction_RightSideSplit {

    public static void main(String[] args) {
        double[] array = new double[40];
        for (int i = 0; i < array.length; i++) {
            array[i] = 2;
        }

        ForkJoinPool pool = new ForkJoinPool();
        SumAction sumAction = new SumAction(array, 0, array.length, null); // when just started, the original array hasn't been divided yet.
        pool.invoke(sumAction);
        System.out.println(sumAction.getResult());

    }
}

class SumAction extends RecursiveAction {
    private double[] array;
    private int low, high;
    // keep referrence of the right half array
    private SumAction rightArray;
    private double result;

    SumAction(double[] array, int low, int high, SumAction rightArray) {  // when start, this array has been cut into two parts, so this variable rightArray is null yet
        this.array = array;
        this.low = low;
        this.high = high;
        this.rightArray = rightArray;
    }

    @Override
    protected void compute() {
        // set new variables so that they can be updated inside the while loop
        int lo = low;
        int hi = high;
        SumAction right = null;
        while (hi-lo > 10) {
           // System.out.println("in while loop first");
            // break up the right half
            int middle = (hi + lo) >>> 1;
            right = new SumAction(array, middle, hi, right); // 2nd right is not null when itself is right half of another array
            right.fork(); // start action  // as far as the array is too long, it will be kept on creating new actions.
            // to keep on sever the array till the right smaller size
            hi = middle;
        }

        double sum = 0;
        for (int i = lo; i < hi; i++) {
           // System.out.println("hi is "+ hi);
            sum += array[i];
        }
        // now work on the right part of array newly created Actions
        while (right != null) {
             right.join(); // wait for the new action to be finished
            sum += right.result;  // get the total sum, this must be before the reassigning of the "right"
            // reassign the right keyword to its own tracking array, so that it will be treated as a whole array
            right = right.rightArray; // loop back to wait for more half arrarys to be finished
        }
        result = sum;

    }

    public double getResult() {
        return result;
    }
}
