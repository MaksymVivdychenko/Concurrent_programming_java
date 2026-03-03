public class Main {
    public static void main(String[] args)
    {
        int[][] matrixA = {
                {1, 2, 3, 2},
                {4, 5, 6, 2},
                {4, 5, 6, 2},
                {4, 5, 6, 2},
                {4, 5, 6, 2}
        };

        int[][] matrixB = {
                {1, 2, 3, 2},
                {4, 5, 6, 2},
                {4, 5, 6, 2},
                {4, 5, 6, 2}
        };
        IMatrixCalculator calc = new SequentialMatrixCalculator();
        var result = calc.MultiplyMatrix(matrixA, matrixB);
        printMatrix(result);
        System.out.print("\n");
        IMatrixCalculator calc2 = new StripeMatrixCalculator(2);
        var result2 = calc2.MultiplyMatrix(matrixA, matrixB);
        printMatrix(result2);
    }

    private static void printMatrix(int[][] matrix)
    {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j <matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
}
