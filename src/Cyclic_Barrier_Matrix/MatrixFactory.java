package Cyclic_Barrier_Matrix;
import java.util.Random;

/**
 * Matrix is a two-dimensional array.
 * Create a random (Row X Length) size of Matrix, count the occurrence of the
 * special number, all within its constructor
 */
public class MatrixFactory {

    private int[][] data;
    private Random rand = new Random();
    MatrixFactory(int row, int column, int theNumber){
        int counter = 0;
        data = new int[row][column];

        for (int i = 0; i <row ; i++) {
            for (int j = 0; j <column; j++) {
               data[i][j] = rand.nextInt(10);
               if(data[i][j]==theNumber){
                   counter++;
               }
            }
        }
        // display total counts of theNumber
        System.out.println("MatrixFactory counter:  " + counter);  // highlight: p134 #7 has been written
    }

    public int[] getTheRowData(int index) {
        return data[index];
    }
}
