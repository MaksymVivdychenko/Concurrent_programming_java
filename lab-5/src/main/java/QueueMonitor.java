import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueueMonitor implements Runnable {
    private final DropQueue queue;
    private final int timeToMonitor;
    private final AtomicBoolean runCondition;
    private final int simulationId;
    private final ArrayList<Integer> queueLengths = new ArrayList<>();

    public QueueMonitor(DropQueue queue, int timeToMonitor, AtomicBoolean runCondition, int simulationId)
    {
        this.queue = queue;
        this.timeToMonitor = timeToMonitor;
        this.runCondition = runCondition;
        this.simulationId = simulationId;
    }

    @Override
    public void run() {
        while (runCondition.get() && !Thread.currentThread().isInterrupted())
        {
            try {
                Thread.sleep(timeToMonitor);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int queueLength = queue.getCurrentQueueSize();
            queueLengths.add(queueLength);
            System.out.println(simulationId + ": Queue size: " + queue.getCurrentQueueSize() + "; totalRequests " + queue.getTotalRequests()
                    + "; totalRejects " + queue.getTotalRejected());

        }
    }

    public SimulationStatistic getStatistic()
    {
        var stats = new SimulationStatistic();
        long totalLength = 0;
        for (var length : queueLengths) {
            totalLength += length;
        }
        stats.meanQueue = (double) totalLength / queueLengths.size();
        stats.rejectionProbability = (double) queue.getTotalRejected() /  queue.getTotalRequests();

        return stats;
    }
}
