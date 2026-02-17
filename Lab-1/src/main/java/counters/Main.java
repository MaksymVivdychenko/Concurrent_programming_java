package counters;

public class Main {
    public static void main(String[] args)
    {
        Counter counter = new Counter();
        System.out.println("Unsafety method:");
        threadUnsafety(counter);
        threadUnsafety(counter);
        System.out.println("Synchronized method:");
        threadSynchronizedMethods(counter);
        System.out.println("Synchronized block:");
        threadSynchronizedBlock(counter);
        System.out.println("Locker:");
        threadLocker(counter);
    }
    private static void threadUnsafety(Counter counter)
    {
        counter.setZeroCounter();
        Thread thread1 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.incrementAsync();
            }
        });
        Thread thread2 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.decrementAsync();
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException _) {

        }
        System.out.println("Counter: " + counter.getCounter());
    }

    private static void threadSynchronizedMethods(Counter counter)
    {
        counter.setZeroCounter();
        Thread thread1 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.incrementSync();
            }
        });
        Thread thread2 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.decrementSync();
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException _) {

        }
        System.out.println("Counter: " + counter.getCounter());
    }

    private static void threadSynchronizedBlock(Counter counter)
    {
        counter.setZeroCounter();
        Thread thread1 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.incrementSyncBlock();
            }
        });
        Thread thread2 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.decrementSyncBlock();
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException _) {

        }
        System.out.println("Counter: " + counter.getCounter());
    }

    private static void threadLocker(Counter counter)
    {
        counter.setZeroCounter();
        Thread thread1 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.incrementSyncObject();
            }
        });
        Thread thread2 = new Thread(() ->
        {
            for (int i = 0; i < 100000; i++)
            {
                counter.decrementSyncObject();
            }
        });
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException _) {

        }
        System.out.println("Counter: " + counter.getCounter());
    }
}