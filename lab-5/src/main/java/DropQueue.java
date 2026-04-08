import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DropQueue {
    private final BlockingQueue<Request> queue;
    private final AtomicLong totalRejected = new AtomicLong(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    public DropQueue(int queueCapacity)
    {
        queue = new ArrayBlockingQueue<Request>(queueCapacity);
    }
    public int getCurrentQueueSize()
    {
        return queue.size();
    }
    public void addRequest(Request r)
    {
        totalRequests.incrementAndGet();
        if(!queue.offer(r)){
            totalRejected.incrementAndGet();
        }
    }

    public Request processRequest() throws InterruptedException {

        return queue.take();
    }

    public long getTotalRejected() {
        return totalRejected.get();
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }
}
