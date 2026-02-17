package task_1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankLock extends BaseBank {
    private final Lock transferLock = new ReentrantLock();
    protected final int[] accounts;
    protected long ntransacts = 0;
    public BankLock(int n, int initialBalance){
        accounts = new int[n];
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i] = initialBalance;
        ntransacts = 0;
    }
    public void transfer(int from, int to, int amount) {
        transferLock.lock();
        try{
            if(accounts[from] >= amount)
            {
                accounts[from] -= amount;
                accounts[to] += amount;
                ntransacts++;
                if (ntransacts % NTEST == 0)
                    test();
            }
        }
        finally {
            transferLock.unlock();
        }
    }
    public void test(){
            int sum = 0;
            for (int account : accounts) sum += account;
            System.out.println("Transactions:" + ntransacts
                    + " Sum: " + sum);
    }
    public int size(){
        return accounts.length;
    }
}
