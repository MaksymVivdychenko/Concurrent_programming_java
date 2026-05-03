import java.util.concurrent.atomic.AtomicInteger;

public class Main2 {
    private static volatile String currentLight = "GREEN";
    private static final AtomicInteger totalCarsPassed = new AtomicInteger(0);
    private static final int TARGET = 10000;

    public static void main(String[] args) {
        Thread trafficLight = new Thread(() -> {
            try {
                while (totalCarsPassed.get() < TARGET) {
                    currentLight = "GREEN";
                    Thread.sleep(70);
                    currentLight = "YELLOW";
                    Thread.sleep(10);
                    currentLight = "RED";
                    Thread.sleep(40);
                    currentLight = "YELLOW";
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread greenFlow = new Thread(() -> carProcess("GREEN"));
        Thread redFlow = new Thread(() -> carProcess("RED"));

        trafficLight.start();
        greenFlow.start();
        redFlow.start();

        try {
            trafficLight.join();
            greenFlow.join();
            redFlow.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Загальна кількість авто: " + totalCarsPassed.get());
    }

    private static void carProcess(String activeSignal) {
        try {
            while (totalCarsPassed.get() < TARGET) {
                if (currentLight.equals(activeSignal)) {
                    go();
                    if (totalCarsPassed.incrementAndGet() >= TARGET) break;
                    Thread.sleep(400);
                }
                Thread.onSpinWait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void go() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}