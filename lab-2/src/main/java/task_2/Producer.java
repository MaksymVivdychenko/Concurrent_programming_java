package task_2;

import java.util.Random;

public class Producer implements Runnable {
    public final Drop drop;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    @Override
    public void run() {
        int[] numbers = new int[100];
        Random random = new Random();
        for (int i = 0; i < numbers.length; i++)
        {
            numbers[i] = i;
        }
        for (int i = 0; i < numbers.length; i++) {
            int putNumber = numbers[random.nextInt(numbers.length)];
            drop.put(putNumber);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException _) {
            }
        }
        drop.put(-1);
    }
}
