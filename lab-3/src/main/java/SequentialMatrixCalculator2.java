public class SequentialMatrixCalculator2 implements IMatrixCalculator{
    public int[][] MultiplyMatrix(int[][] a, int[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;

        int[][] c = new int[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int k = 0; k < colsA; k++) {
                int tempA = a[i][k];
                for (int j = 0; j < colsB; j++) {
                    c[i][j] += tempA * b[k][j];
                }
            }
        }

        return c;
    }
}