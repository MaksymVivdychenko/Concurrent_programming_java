import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer implements Runnable {
    private final DropQueue _queue;
    private final int timeForRequest;
    private final String consumerName;
    private final AtomicBoolean runCondition;

    public Consumer(DropQueue queue, int timeForRequest, String consumerName, AtomicBoolean runCondition) {
        this._queue = queue;
        this.timeForRequest = timeForRequest;
        this.consumerName = consumerName;
        this.runCondition = runCondition;
    }


    @Override
    public void run() {
        while (runCondition.get()) {
            try {
               var request = _queue.processRequest();
               // System.out.println(consumerName + " process request with id " + request.id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(timeForRequest);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
