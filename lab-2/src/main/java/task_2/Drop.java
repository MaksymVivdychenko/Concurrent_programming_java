package task_2;

import java.util.ArrayList;

public class Drop {
    private int _number;
    private boolean isEmpty= true;

    public synchronized void put(int number)
    {
        while(!isEmpty)
        {
            try{
                wait();
            } catch (Exception _) {}
        }
        isEmpty = false;
        _number = number;
        notifyAll();
    }

    public synchronized int take(){
        while(isEmpty)
        {
            try{
                wait();
            } catch (InterruptedException _) {}
        }
        isEmpty = true;
        notifyAll();
        return _number;
    }
}
