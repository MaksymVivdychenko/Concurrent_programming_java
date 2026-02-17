package task_2;

import java.util.Random;

public class Consumer implements Runnable {
    private final Drop drop;

    public Consumer(Drop drop) {
        this.drop = drop;
    }

    @Override
    public void run() {
        Random random = new Random();
        int i = drop.take();
        while(i != -1)
        {
            System.out.println("Consumer get number: " + i);
            i = drop.take();
            try{
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException _) {
            }
        }
    }
}
