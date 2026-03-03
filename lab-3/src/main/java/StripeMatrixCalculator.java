import java.util.ArrayList;

public class StripeMatrixCalculator implements IMatrixCalculator {
    private int threadCount;

    public StripeMatrixCalculator(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public int[][] MultiplyMatrix(int[][] a, int[][] b) {
        int linesPerThread = a.length / threadCount;
        Thread[] threads = new Thread[threadCount];
        int[][] c = new int[a.length][b[0].length];
        int startLine = 0;
        int endLine = startLine + linesPerThread;
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            threads[i] = new Thread(() ->
            {
                for (int line = startLine; line < endLine; line++) {

                }
                for (int colCounter = 0, j = finalI; colCounter < b[0].length; colCounter++) {
                    if (j < 0) {
                        j = b[0].length - 1;
                    }
                    for (int k = 0; k < b.length; k++) {
                        c[finalI][j] += a[finalI][k] * b[k][j];
                    }
                    j--;
                }
            });
            threads[i].start();
        }
        for (var t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return c;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
}
