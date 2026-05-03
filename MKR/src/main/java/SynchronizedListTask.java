import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedListTask {
    public static void main(String[] args) throws InterruptedException {
        List<String> sharedList = new ArrayList<>();
        Lock lock = new ReentrantLock();
        int targetSize = 10000;
        int threadsCount = 7;
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threadsCount; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                int wordLength = id + 1;
                while (true) {
                    lock.lock();
                    try {
                        if (sharedList.size() >= targetSize) break;
                    } finally {
                        lock.unlock();
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < wordLength; j++) {
                        char randomChar = (char) ThreadLocalRandom.current().nextInt('a', 'z' + 1);
                        sb.append(randomChar);
                    }
                    String word = sb.toString();

                    lock.lock();
                    try {
                        if (sharedList.size() < targetSize) {
                            sharedList.add(word);
                        } else {
                            break;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Final list size: " + sharedList.size());
    }
}