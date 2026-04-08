import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Producer implements Runnable{
    private final DropQueue queue;
    private final GaussingDistribution gaussingDistribution;
    private final AtomicBoolean runCondition;

    public Producer(DropQueue queue, GaussingDistribution gaussingDistribution, AtomicBoolean runCondition)
    {
        this.queue = queue;
        this.gaussingDistribution = gaussingDistribution;
        this.runCondition = runCondition;
    }
    @Override
    public void run() {
        while (runCondition.get())
        {
            queue.addRequest(new Request((int) (Math.random() * 100000)));
            try {
                var sleepTime = gaussingDistribution.nextGaussian();
                Thread.sleep((int)sleepTime);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
