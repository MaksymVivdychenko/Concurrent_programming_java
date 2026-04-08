import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Simulation implements Callable<SimulationStatistic> {
    private final int consumerCount;
    private final int consumerTimerMs;
    private final int simulationTimerS;
    private final int queueCapacity;
    private final int monitorTimerMs;

    public Simulation(int consumerCount, int consumerTimerMs, int simulationTimerS, int queueCapacity, int monitorTimerMs) {
        this.consumerCount = consumerCount;
        this.consumerTimerMs = consumerTimerMs;
        this.simulationTimerS = simulationTimerS;
        this.queueCapacity = queueCapacity;
        this.monitorTimerMs = monitorTimerMs;
    }

    @Override
    public SimulationStatistic call() {
        var consumerTimerMs = 50;
        var simulationTimerS = 3;
        var queueCapacity = 5;
        var monitorTimerMs = 150;

        AtomicBoolean runCondition = new AtomicBoolean(true);
        GaussingDistribution dg = new GaussingDistribution(10, 2);
        DropQueue dropQueue = new DropQueue(queueCapacity);
        Producer producer = new Producer(dropQueue, dg, runCondition);
        var consumerPool = Executors.newFixedThreadPool(consumerCount);
        for (int i = 0; i < consumerCount; i++) {
            consumerPool.submit(new Consumer(dropQueue, consumerTimerMs, "Consumer" + (i + 1), runCondition));
        }
        consumerPool.shutdown();
        QueueMonitor monitor = new QueueMonitor(dropQueue, 150, runCondition);
        Thread producerThread = new Thread(producer);
        Thread monitorThread = new Thread(monitor);
        producerThread.start();
        monitorThread.start();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            runCondition.set(false);
            scheduler.shutdown();
        }, simulationTimerS, TimeUnit.SECONDS);
        try {
            scheduler.awaitTermination(monitorTimerMs + 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return monitor.getStatistic();
    }
}
