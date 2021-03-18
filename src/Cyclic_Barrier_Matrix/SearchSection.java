package Cyclic_Barrier_Matrix;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * For each subSection, search
 */

public class SearchSection implements Runnable {
    private Result result;
    private int firstRow, lastRow;
    private CyclicBarrier cyclicBarrier;
    private MatrixFactory matrixFactory;
    private int specialNum;

    public SearchSection(Result result, int firstRow, int lastRow,MatrixFactory matrixFactory,CyclicBarrier cyclicBarrier, int specialNum){
        this.result = result;
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.cyclicBarrier = cyclicBarrier;
        this.matrixFactory = matrixFactory;
        this.specialNum = specialNum;
    }
    // do math and collect the counter into the Result
    @Override
    public void run() {
        //int counter = 0;
        for (int i = firstRow; i <lastRow ; i++) {
            int[] row = matrixFactory.getTheRowData(i);
           // System.out.println("SearchSection ROW is "+ i);
            int counter = 0;

            for (int j = 0; j < row.length; j++) {
               if(row[j] == specialNum) {
                  counter++;
               }
            }
            result.setConunter(i,counter);
            //System.out.println("SearchSection row" + i + " counter is set");
        }

        // list this thread into the cyclicBarrier
        try{
            System.out.println(Thread.currentThread().getName() + " is waiting");
            cyclicBarrier.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }
}
