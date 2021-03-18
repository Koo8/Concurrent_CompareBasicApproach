package Cyclic_Barrier_Matrix;

public class SumUp implements Runnable {
    private Result result;
    private int totalCounter =0;
    public SumUp(Result result) {
        this.result = result;
    }


    @Override
    public void run() {
        for (int i = 0; i <result.getSize() ; i++) {
            totalCounter+= result.getCounter(i);
        }
        System.out.println("Total counter is " + totalCounter);

    }
}
