import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ProducerConsumer {
    public static void main(String[] args) {
        List<Object> buffer = Collections.synchronizedList(new LinkedList<>());
        int capacity = 10;

        Runnable producer = () -> {
            try {
                while (true) {
                    synchronized (buffer) {
                        while (buffer.size() == capacity) {
                            buffer.wait();
                        }
                        buffer.add(new Object());
                        System.out.println(Thread.currentThread().getName() + " додає. Розмір: " + buffer.size());
                        buffer.notifyAll();
                    }
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    synchronized (buffer) {
                        while (buffer.isEmpty()) {
                            buffer.wait();
                        }
                        Object obj = buffer.remove(0);
                        System.out.println(Thread.currentThread().getName() + " вилучає. Розмір: " + buffer.size());
                        buffer.notifyAll();
                    }
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread threadA = new Thread(producer, "A");
        Thread threadB = new Thread(producer, "B");
        Thread threadC = new Thread(consumer, "C");

        threadA.start();
        threadB.start();
        threadC.start();
    }
}