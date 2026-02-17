package counters;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int counter = 0;
    private final Object sync = new Object();
    private final Lock locker = new ReentrantLock();
    public void incrementAsync()
    {
        counter++;
    }
    public void decrementAsync()
    {
        counter--;
    }

    public synchronized void incrementSync()
    {
        counter++;
    }

    public synchronized void decrementSync()
    {
        counter--;
    }

    public void incrementSyncBlock()
    {
        synchronized (sync)
        {
            counter++;
        }
    }

    public void decrementSyncBlock()
    {
        synchronized (sync)
        {
            counter--;
        }
    }

    public void incrementSyncObject()
    {
        locker.lock();
        try {
            counter++;
        }finally {
            locker.unlock();
        }
    }

    public void decrementSyncObject()
    {
        locker.lock();
        try {
            counter--;
        }finally {
            locker.unlock();
        }
    }


    public int getCounter() { return counter; }
    public void setZeroCounter() { counter = 0; }
}
