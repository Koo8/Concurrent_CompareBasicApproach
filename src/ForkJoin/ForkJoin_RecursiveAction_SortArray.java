package ForkJoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * Use forkJoinPool - RecursiveAction to sort an array of long, use
 * fork() join().
 * ">>> 1" ----> unsigned right shift operator equals to divide by 2
 */
public class ForkJoin_RecursiveAction_SortArray {

    public static void main(String[] args) {

        ForkJoinPool pool = new ForkJoinPool();
        long[] array = new long[30];
        for (int i = 0; i <array.length ; i++) {
            array[i]= (long) (Math.random()*50);
        }
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+ " ");
        }
        Counter counter = new Counter();
        System.out.println("\n-------------------------");
        // sortTask
        SortTask task = new SortTask(array, 0, array.length, counter);
       // ForkJoinTask<Void> job =
                //pool.submit(task).join();
        // or use invoke() that has join() builtin.
                pool.invoke(task);
       // job.join();
        for (int i = 0; i <array.length ; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("\nIncresed Array ");

        // increaseTask
        IncrementTask t = new IncrementTask(array, 0, array.length);
        ForkJoinTask<Void> f = pool.submit(t);
        f.join();
        for (int i = 0; i <array.length ; i++) {
            System.out.print(array[i] + " ");
        }
        pool.shutdown();

        System.out.println("\nTotal "+ counter.getCount() + " times of sorting");

    }
}

class SortTask extends RecursiveAction {
    // two types of constructors
    private final long[] array;
    private final int low, high;
    private Counter counter;

    SortTask(long[] array, int low, int high,Counter counter) {
        this.array = array;
        this.low = low;
        this.high = high;
        this.counter =counter;
    }

    SortTask(long[] array, Counter counter) {
        // an override constructor based on the first constructor
        this(array, 0, array.length, counter);
    }

    @Override
    protected void compute() {
        int THRESHOLD = 11;
        if (high - low < THRESHOLD) {
            counter.increment();
            System.out.println("Compute() there are "+ (high-low)+ " numbers ");
            sortSequentially(low, high);
        } else {
            //oo highlight :get middle index
            int middle = (high + low) >>> 1;  // equals to (high+low)/2;
           // int middle1 = low + ((high - low) >> 1);  highlight: this works out the same.
          //  System.out.println("Compute() middle index is "+ middle+ " high index is "+ high + " low index is "+ low);
            invokeAll(new SortTask(array, low, middle,counter),  //NOTE: both forkJoinTask and forkJoinPool have invokeAll(),return different result
                    new SortTask(array, middle, high,counter));

//            merges(middle);
//             or
            merger (low, middle, high);
        }
    }
    // method 1
    void merges(int middle) {
        if (array[middle - 1] < array[middle]) {
            return; // the arrays are already correctly sorted, so we can skip the merge
        }
        long[] copy = new long[high - low];
        System.arraycopy(array, low, copy, 0, copy.length);
        int copyLow = 0;
        int copyHigh = high - low;
        int copyMiddle = middle - low;

        for (int i = low, p = copyLow, q = copyMiddle; i < high; i++) {
            if (q >= copyHigh || (p < copyMiddle && copy[p] < copy[q]) ) {
                array[i] = copy[p++];
            } else {
                array[i] = copy[q++];
            }
        }
    }
    // method 2
    private void merger(int lo, int mid, int hi) {
        System.out.println("low index " + lo + " mid index "+ mid + " high index "+ hi);
        if (array[mid - 1] < array[mid]) {
            return; // the arrays are already correctly sorted, so we can skip the merge
        }
        // copy the lower sorted array , make it compare with the other half of array,
        long[] buf = Arrays.copyOfRange(array, lo, mid);
        // highlight: This loop is a complex, it follows i, but i may not change every loop, instead k incremented, for that case, k==hi to determine def[i] is selected.
        for (int i = 0, j = lo, k = mid; i < buf.length/*this is the condition, i has to be smaller than length*/; j++){  // although buf.length is 3, since it is for i<3, there are times i is not changed but k changed, so the loop will keep on adding j++ each time.
            System.out.println("merger is doing index "+ j);
            array[j] = (k == hi || buf[i] < array[k]) ?  // hi is the last index+1;
                    buf[i++] : array[k++];  // highlight: in case i reached the max(length), but k is still not up to the hi, the array[k] should keep its original value without any further comparison
        }
        System.out.println("\nmerger is done. " + "low index " + lo + " mid index "+ mid + " high index "+ hi);

    }

    private void sortSequentially(int low, int high) {
        // Arrays.sort() -> Dual-Pivot Quicksort
        Arrays.sort(array, low, high);
    }
}
class IncrementTask extends RecursiveAction {

    private long[] array;
    private int low, high;
    public IncrementTask(long[] array, int low, int high){
        this.array = array;
        this.low = low;
        this.high = high;
    }
    @Override
    protected void compute() {
         int Threshold = 10;
         if(high -low < Threshold) {
             for (int i = low; i <high ; i++) {
                 array[i]++;
             }
         }  else {
             int middle = (high+low)>>>1;
             invokeAll(new IncrementTask(array, low, middle), new IncrementTask(array, middle, high));
         }

    }
}




