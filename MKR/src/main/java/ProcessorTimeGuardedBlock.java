import java.util.concurrent.atomic.AtomicInteger;

public class ProcessorTimeGuardedBlock {
    private String state = "w";
    private long countdownMs = 1000;
    private boolean running = true;
    private long lastTimestamp;

    public synchronized void toggleState() {
        state = state.equals("r") ? "w" : "r";

        if (state.equals("r")) {
            lastTimestamp = System.currentTimeMillis();
            notifyAll();
        }
        System.out.println("\n[Потік А] Стан змінено на: " + state);
    }

    public synchronized void processCountdown() throws InterruptedException {
        while (state.equals("w") && running) {
            wait();
            lastTimestamp = System.currentTimeMillis();
        }

        if (!running || countdownMs <= 0) return;
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastTimestamp;

        if (elapsed > 0) {
            countdownMs -= elapsed;
            lastTimestamp = currentTime;

            if (countdownMs <= 0) {
                countdownMs = 0;
                running = false;
                System.out.println("\n[Потік В] Час вичерпано (0ms). Зупинка.");
                notifyAll();
            } else {
                System.out.print(countdownMs + "ms ");
            }
        }
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public static void main(String[] args) {
        ProcessorTimeGuardedBlock monitor = new ProcessorTimeGuardedBlock();

        Thread threadA = new Thread(() -> {
            try {
                while (monitor.isRunning()) {
                    Thread.sleep(100);
                    monitor.toggleState();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                while (monitor.isRunning()) {
                    monitor.processCountdown();
                    // Мінімальна пауза, щоб не перевантажувати процесор в стані "r"
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadA.start();
        threadB.start();
    }
}