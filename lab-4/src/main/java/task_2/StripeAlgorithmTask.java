package task_2;

import java.util.concurrent.RecursiveAction;

public class StripeAlgorithmTask extends RecursiveAction {
    private final int TRESHOLD = 50;
    private final int[][] A;
    private final int[][] B;
    private final int[][] C;
    private final int startRow;
    private final int endRow;

    public StripeAlgorithmTask(int[][] A, int[][] B, int[][] C, int startRow,
                               int endRow) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    protected void compute() {
        if(endRow - startRow < TRESHOLD)
        {
            for (int row = startRow; row < endRow; row++) {
                for (int colCounter = 0, j = row; colCounter < B[0].length; colCounter++) {
                    if (j < 0 || j >=B[0].length) {
                        j = B[0].length - 1;
                    }
                    for (int k = 0; k < B.length; k++) {
                        C[row][j] += A[row][k] * B[k][j];
                    }
                    j--;
                }
            }

            return;
        }

        int middleRow = (endRow - startRow) / 2 + startRow;
        var firstTask = new StripeAlgorithmTask(A, B, C, startRow, middleRow);
        var secondTask = new StripeAlgorithmTask(A, B, C,
                middleRow, endRow);
        firstTask.fork();
        secondTask.compute();
        firstTask.join();
    }
}
