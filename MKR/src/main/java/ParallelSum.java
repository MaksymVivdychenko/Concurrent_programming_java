import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelSum {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int n = 1000007;
        int h = 4;
        int m = 10;
        double[] array = new double[n];
        for (int i = 0; i < n; i++) array[i] = 1.0;

        ExecutorService executor = Executors.newFixedThreadPool(h);
        List<Callable<Double>> tasks = new ArrayList<>();

        int baseSize = n / m;
        int remainder = n % m;
        int currentStart = 0;

        for (int i = 0; i < m; i++) {
            int chunkSize = baseSize + (i < remainder ? 1 : 0);
            if (chunkSize == 0) break;

            final int start = currentStart;
            final int end = start + chunkSize;
            currentStart = end;

            tasks.add(() -> {
                double localSum = 0;
                for (int j = start; j < end; j++) {
                    localSum += array[j];
                }
                return localSum;
            });
        }

        List<Future<Double>> results = executor.invokeAll(tasks);

        double totalSum = 0;
        for (Future<Double> result : results) {
            totalSum += result.get();
        }

        System.out.println("Total Sum: " + totalSum);
        executor.shutdown();
    }
}