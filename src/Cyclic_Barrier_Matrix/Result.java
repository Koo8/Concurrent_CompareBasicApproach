package Cyclic_Barrier_Matrix;

/**
 * For each row of a matrix, count the occurrence of a specific number;
 * store this counter into an array
 */
public class Result {
    private int[] counterArray;

    public Result(int size){
        counterArray = new int[size];
    }

    public int getSize() {
        return counterArray.length;
    }

    public void setConunter(int index, int number) {
        counterArray[index] = number;
    }

    public int getCounter(int index) {
        return counterArray[index];
    }
}
