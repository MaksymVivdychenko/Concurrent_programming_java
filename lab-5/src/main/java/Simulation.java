import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Simulation implements Callable<SimulationStatistic> {
    private final int consumerCount;
    private final int consumerTimerMs;
    private final int simulationTimerS;
    private final int queueCapacity;
    private final int monitorTimerMs;
    private final int simulationId;
    private final int producerTimerMeanMs;
    private final int producerTimerSdMs;

    public Simulation(int consumerCount, int consumerTimerMs, int simulationTimerS, int queueCapacity, int monitorTimerMs,
                      int simulationId, int producerTimerMeanMs, int producerTimerSdMs) {
        this.consumerCount = consumerCount;
        this.consumerTimerMs = consumerTimerMs;
        this.simulationTimerS = simulationTimerS;
        this.queueCapacity = queueCapacity;
        this.monitorTimerMs = monitorTimerMs;
        this.simulationId = simulationId;
        this.producerTimerMeanMs = producerTimerMeanMs;
        this.producerTimerSdMs = producerTimerSdMs;
    }

    @Override
    public SimulationStatistic call() {

        AtomicBoolean runCondition = new AtomicBoolean(true);
        GaussingDistribution dg = new GaussingDistribution(producerTimerMeanMs, producerTimerSdMs);
        DropQueue dropQueue = new DropQueue(queueCapacity);
        Producer producer = new Producer(dropQueue, dg, runCondition);
        var consumerPool = Executors.newFixedThreadPool(consumerCount);
        for (int i = 0; i < consumerCount; i++) {
            consumerPool.submit(new Consumer(dropQueue, consumerTimerMs, "Consumer" + (i + 1), runCondition));
        }
        consumerPool.shutdown();
        QueueMonitor monitor = new QueueMonitor(dropQueue, monitorTimerMs, runCondition, simulationId);
        Thread producerThread = new Thread(producer);
        Thread monitorThread = new Thread(monitor);
        producerThread.start();
        monitorThread.start();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            runCondition.set(false);
            scheduler.shutdownNow();
        }, simulationTimerS, TimeUnit.SECONDS);

        scheduler.shutdown();

        try {
            consumerPool.awaitTermination(simulationTimerS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return monitor.getStatistic();
    }
}
