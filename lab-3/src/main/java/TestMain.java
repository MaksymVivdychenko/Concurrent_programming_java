public class TestMain {
    public static void main(String[] args)
    {
        int[] matrixSizes = new int[] {500, 1000, 1500, 2000, 2500, 3000};
        IMatrixCalculator calc = new StripeMatrixCalculator(9);
        for (int size : matrixSizes)
        {
            int[][] A = Main.generateSquareMatrix(size);
            int[][] B = Main.generateSquareMatrix(size);
            int times = 5;
            long duration = 0;
            for (int i = 0; i < times; i++) {
                long startTime = System.nanoTime();
                calc.MultiplyMatrix(A, B);
                long endTime = System.nanoTime();
                duration += (endTime - startTime) / 1000000;
            }
            long avgTime = duration / times;
            System.out.println( " for " + size + "  : " + avgTime + "ms");
        }
    }
}
