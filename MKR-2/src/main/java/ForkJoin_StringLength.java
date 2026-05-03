import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoin_StringLength {
    private final static Random rand = new Random();
    public static final int PARALLELISM = 10;

    public static void main(String[] args) {
        var words = generateRandomListFixedWordLength(10000);
        var task = new AvgStringLengthCalc(words);
        System.out.println(task.calcAvgStringLength(PARALLELISM));
    }

    //Генерує слова визначеної довжини для швидкості тестування
    public static ArrayList<String> generateRandomListFixedWordLength(int size) {
        ArrayList<String> randomList = new ArrayList<>();
        Random rand = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < size; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 9; j++) {
                sb.append(characters.charAt(rand.nextInt(characters.length())));
            }
            randomList.add(sb.toString());
        }

        return randomList;
    }
}

class StringLengthTask extends RecursiveTask<Long> {
    private final int THRESHOLD = 1000;
    private final ArrayList<String> words;
    private final int start;
    private final int end;

    public StringLengthTask(ArrayList<String> words, int start, int end) {
        this.words = words;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        long sum = 0;
        if (end - start <= THRESHOLD) {
            for (int i = start; i < end; i++) {
                sum += words.get(i).length();
            }
            return sum;
        }

        int middle = start + (end - start) / 2;

        var leftTask = new StringLengthTask(words, start, middle);
        var rightTask = new StringLengthTask(words, middle, end);
        leftTask.fork();
        var rightTaskResult = rightTask.compute();
        var leftTaskResult = leftTask.join();

        return rightTaskResult + leftTaskResult;
    }
}

class AvgStringLengthCalc {
    private ArrayList<String> words;

    public AvgStringLengthCalc(ArrayList<String> words) {
        this.words = words;
    }

    public double calcAvgStringLength(int parallelism) {
        long totalLength;
        try (var pool = new ForkJoinPool(parallelism)) {
            totalLength = pool.invoke(new StringLengthTask(words, 0, words.size()));
            pool.shutdown();
        }
        return (double) totalLength / words.size();
    }
}