package Cyclic_Barrier_Matrix;

import java.util.concurrent.CyclicBarrier;

/**
 * NOTE: Phaser is a lot simpler than CyclicBarrier (still need to handle interruption),
 * which in turn is better than lockDownLatch (can't be reused) --> check the comparision
 * https://www.javaspecialists.eu/archive/Issue257-CountDownLatch-vs-CyclicBarrier-vs-Phaser.html
 *
 *  use CyclicBarrier to divide and conquer the count of a targetNumber.
 * All sebTasks need to assign the same barrier and await() for the common point of completion
 * A final task(SumUp) can be added as the parameter for the barrier to implement
 * after all subTasks have all completed.
 */

public class Matrix_Main {

    public static void main(String[] args) {
        // instantiate all the variables
        final int row = 1000;
        final int column = 1000;
        final int targetNum = 7;
        final int threads = 5;
        final int eachSection = row/threads;
        
        MatrixFactory matrixFactory = new MatrixFactory(row, column, targetNum);
        Result result = new Result(row);
        SumUp sumUp = new SumUp(result);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threads, sumUp);
        for (int i = 0; i <threads ; i++) {
            SearchSection s = new SearchSection(result, i*eachSection, (i+1)*eachSection,matrixFactory,cyclicBarrier,targetNum ); // all threads are listed with this barrier
            Thread thread = new Thread(s, "t"+ i);
            thread.start();
        }

        



    }
}
