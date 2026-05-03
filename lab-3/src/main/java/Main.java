import java.util.HashMap;
import java.util.Random;

public class Main {
    public static void main(String[] args)
    {
        HashMap<String, IMatrixCalculator> calculators = new HashMap<>();
        calculators.put("Sequential", new SequentialMatrixCalculator());
        calculators.put("Stripe x4", new StripeMatrixCalculator(4));
        calculators.put("Stripe x9", new StripeMatrixCalculator(9));
        calculators.put("Stripe x16", new StripeMatrixCalculator(16));
        calculators.put("Fox x4", new FoxMatrixCalculator(4));
        calculators.put("Fox x9", new FoxMatrixCalculator(9));
        calculators.put("Fox x16", new FoxMatrixCalculator(16));
        int[] matrixSizes = new int[] {500, 1000, 1500, 2000, 2500, 3000};
        for (var calc : calculators.entrySet())
        {
            for (int size : matrixSizes)
            {
                int[][] A = generateSquareMatrix(size);
                int[][] B = generateSquareMatrix(size);
                int times = 5;
                long duration = 0;
                for (int i = 0; i < times; i++) {
                    long startTime = System.nanoTime();
                    calc.getValue().MultiplyMatrix(A, B);
                    long endTime = System.nanoTime();
                    duration += (endTime - startTime) / 1000000;
                }
                long avgTime = duration / times;
                System.out.println(calc.getKey() +  " for " + size + "  : " + avgTime + "ms");
            }
        }
    }

    public static int[][] generateSquareMatrix(int n) {
        int[][] matrix = new int[n][n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = rand.nextInt(1000);
            }
        }
        return matrix;
    }
}
