package task_2;

public class Console {
    public static void main(String[] args)
    {
        Drop drop = new Drop();
        Consumer consumer = new Consumer(drop);
        Producer producer = new Producer(drop);
        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(consumer);
        t1.start();
        t2.start();
    }
}
