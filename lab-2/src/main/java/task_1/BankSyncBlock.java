package task_1;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

public class BankSyncBlock extends BaseBank {
    private final Object locker = new Object();
    protected final int[] accounts;
    protected long ntransacts = 0;

    public BankSyncBlock(int n, int initialBalance) {
        accounts = new int[n];
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i] = initialBalance;
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount) {
        synchronized (locker) {
            if (accounts[from] >= amount) {
                accounts[from] -= amount;
                accounts[to] += amount;
                ntransacts++;
                if (ntransacts % NTEST == 0)
                    test();
            }
        }
    }

    public synchronized void test() {
        int sum = 0;
        for (int account : accounts) sum += account;
        System.out.println("Transactions:" + ntransacts
                + " Sum: " + sum);
    }

    public int size() {
        return accounts.length;
    }
}

