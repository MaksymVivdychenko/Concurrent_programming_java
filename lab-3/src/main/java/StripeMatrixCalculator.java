import java.util.ArrayList;

public class StripeMatrixCalculator implements IMatrixCalculator {
    private int threadCount;

    public StripeMatrixCalculator(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public int[][] MultiplyMatrix(int[][] a, int[][] b) {
        int remainder = a.length % threadCount;
        int linesPerThread = a.length / threadCount;
        Thread[] threads = new Thread[threadCount];
        int[][] c = new int[a.length][b[0].length];
        int startLine;
        int endLine = 0;
        for (int i = 0; i < threadCount; i++) {
            startLine = endLine;
            endLine = startLine + linesPerThread;
            if(remainder > 0)
            {
                endLine++;
                remainder--;
            }
            if (endLine > a.length) {
                endLine = a.length;
            }
            int finalStartLine = startLine;
            int finalEndLine = endLine;
            threads[i] = new Thread(() ->
            {
                for (int line = finalStartLine; line < finalEndLine; line++) {
                    for (int colCounter = 0, j = line; colCounter < b[0].length; colCounter++) {
                        if (j < 0 || j >= b[0].length) {
                            j = b[0].length - 1;
                        }
                        for (int k = 0; k < b.length; k++) {
                            c[line][j] += a[line][k] * b[k][j];
                        }
                        j--;
                    }
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
}
