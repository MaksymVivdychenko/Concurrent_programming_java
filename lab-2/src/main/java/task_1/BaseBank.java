package task_1;

public abstract class BaseBank {
    public static final int NTEST = 100;

    public abstract void transfer(int from, int to, int amount);
    public abstract void test();
    public abstract int size();
}