package task_4;

public class SyncThreads {
    private static int type = 0;
    private static final Object lock = new Object();
    public static void main(String[] args) {
        Thread t1 = new Thread(SyncThreads::print1);
        Thread t2 = new Thread(SyncThreads::print2);
        Thread t3 = new Thread(SyncThreads::print3);
        t1.start();
        t2.start();
        t3.start();
    }
    private static void print1()
    {
        synchronized (lock)
        {
            for (int i = 0; i < 1000; i++) {
                while (type != 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException _) {
                    }
                }
                System.out.print("|");
                type = 1;
                lock.notifyAll();
            }
        }
    }
    private static void print2()
    {
        synchronized (lock)
        {
            for (int i = 0; i < 1000; i++) {
                while(type != 1)
                {
                    try {
                        lock.wait();
                    } catch (InterruptedException _) {
                    }
                }
                System.out.print("\\");
                type = 2;
                lock.notifyAll();
            }
        }
    }
    private static void print3() {
        synchronized (lock) {
            for (int i = 0; i < 1000; i++) {
            while (type != 2) {
                try {
                    lock.wait();
                } catch (InterruptedException _) {
                }
            }
            System.out.print("/");
            type = 0;
            lock.notifyAll();
            }
        }
    }
}
