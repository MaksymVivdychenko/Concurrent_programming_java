import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoin_sum {
    public static final int ARRAY_SIZE = 100000;
    public static final int PARALLELISM = 12;
    public static void main(String[] args) {

        var array = getArray(ARRAY_SIZE);
        Double sumResult;
        try (var forkJoinFramework = new ForkJoinPool(PARALLELISM)) {
            sumResult = forkJoinFramework.invoke(new SumArrayTask(array, 0, array.length));
            forkJoinFramework.shutdown();
        }
        System.out.println(sumResult);
    }

    private static double[] getArray(int size)
    {
        var array = new double[size];
        Arrays.fill(array, 1);
        return array;
    }
}

class SumArrayTask extends RecursiveTask<Double> {
    private final double[] array;
    private final int start;
    private final int end;
    private final int THRESHOLD = 5000;

    public SumArrayTask(double[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Double compute() {
        if (end - start <= THRESHOLD) {
            double sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }

        int middle = start + (end - start) / 2;

        var leftTask = new SumArrayTask(array, start, middle);
        var rightTask = new SumArrayTask(array, middle, end);
        leftTask.fork();
        var rightTaskResult = rightTask.compute();
        var leftTaskResult = leftTask.join();

        return rightTaskResult + leftTaskResult;
    }
}
