package task_4;

public class AsyncThreads {
    private static int type = 0;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> print("|"));
        Thread t2 = new Thread(() -> print("\\"));
        Thread t3 = new Thread(() -> print("/"));
        t1.start();
        t2.start();
        t3.start();
    }

    private static void print(String el) {
        for (int i = 0; i < 1000; i++) {
            System.out.print(el);
        }
    }
}