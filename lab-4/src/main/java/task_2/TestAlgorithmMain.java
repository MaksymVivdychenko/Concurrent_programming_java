package task_2;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class TestAlgorithmMain {
    public static void main(String[] args) {

        try (ForkJoinPool pool = new ForkJoinPool()) {
            int n = 1500;
            int[][] A = generateSquareMatrix(n);
            int[][] B = generateSquareMatrix(n);
            int[][] C = new int[n][n];
            var matrixMultiplicationTask = new StripeAlgorithmTask(A, B, C, 0, A.length);
            pool.invoke(matrixMultiplicationTask);
            pool.shutdown();
            var sequentialMatrixMulti = new SequentialMatrixMultiplication();
            int[][] C2 = sequentialMatrixMulti.MultiplyMatrix(A, B);
            System.out.print(compareMatrix(C, C2));

        }
    }

    public static int[][] generateSquareMatrix(int n) {
        int[][] matrix = new int[n][n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextInt(2000);
            }
        }
        return matrix;
    }

    public static boolean compareMatrix(int[][] A, int[][] B)
    {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                if(A[i][j] != B[i][j])
                {
                    return false;
                }
            }

        }
        return true;
    }

}


