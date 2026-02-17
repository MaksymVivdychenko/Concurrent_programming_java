import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

public class BilliardBoard {
    private ArrayList<Ball> balls = new ArrayList<Ball>();
    private ArrayList<Hole> holes = new ArrayList<Hole>();
    private List<Thread> activeThreads = new ArrayList<>();
    private BounceFrame frame;
    private int droppedBalls = 0;
    private synchronized void incrementCounter()
    {
        droppedBalls++;
        frame.updateScorePanel(droppedBalls);
    }

    public synchronized void addBall(Ball ball)
    {
        balls.add(ball);
    }
    public synchronized void removeBall(Ball ball)
    {
        balls.remove(ball);
    }
    public boolean isInHole(Ball ball)
    {
        for (Hole hole : holes) {
            double dx = ball.getX() - hole.getX();
            double dy = ball.getY() - hole.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < hole.getXSize() / 1.5) {
                incrementCounter(); // Рахуємо очки
                return true;
            }
        }
        return false;
    }
    public synchronized ArrayList<Ball> getBalls()
    {
        return new ArrayList<>(balls);
    }
     public ArrayList<Hole> getHoles() {return holes;}

    public void startBall()
    {
        for (int i = 0; i < 1000; i++) {
            Ball b = createBall(Color.BLUE);
            balls.add(b);
            BallThread thread = new BallThread(b, frame.getCanvas(), this);
            activeThreads.add(thread);
            thread.start();
            //System.out.println("Thread name = " + thread.getName());
        }
    }

    public void stopGame()
    {
        for(Thread b : activeThreads)
        {
            b.interrupt();
        }
        balls.clear();
    }

    public void priorityTest()
    {
        Ball b = new Ball(100, 100, 60, 60, Color.red);
        balls.add(b);
        BallThread thread = new BallThread(b, frame.getCanvas(), this);
        thread.setPriority(MAX_PRIORITY);
        thread.start();

        for (int i = 0; i < 2000; i++) {
            Ball b1 = new Ball(100, 100, 40, 40, Color.BLUE);
            balls.add(b1);
            BallThread thread1 = new BallThread(b1, frame.getCanvas(), this);
            activeThreads.add(thread1);
            thread1.setPriority(MIN_PRIORITY);
            thread1.start();
            //System.out.println("Thread name = " + thread.getName());
        }
        //System.out.println("Thread name = " + thread.getName());
    }

    public void joinTest() {
        new Thread(() -> {
            Ball bBlue = new Ball(100, 100, 40, 40, Color.blue);
            balls.add(bBlue);
            BallThread threadBlue = new BallThread(bBlue, frame.getCanvas(), this);
            activeThreads.add(threadBlue);
            threadBlue.start();
            try {
                threadBlue.join();
            } catch (InterruptedException _) {
            }

            Ball bRed = new Ball(100, 100, 40, 40, Color.red);
            balls.add(bRed);
            BallThread threadRed = new BallThread(bRed, frame.getCanvas(), this);
            activeThreads.add(threadRed);
            threadRed.start();
        }).start();
    }

    private Ball createBall(Color color)
    {
        int x, y;
        if (Math.random() < 0.5) {
            x = 100 + new Random().nextInt(this.frame.getCanvas().getWidth() - 100);
            y = 100;
        } else {
            x = 100;
            y = 100 + new Random().nextInt(this.frame.getCanvas().getHeight() - 100);
        }
        return new Ball(x, y, 40, 40, color);
    }
    public void setHoles(Component canvas)
    {
        int holeXSize = 80;
        int holeYSize = 80;
        Color holeColor = Color.BLACK;
        ArrayList<Hole> holes = new ArrayList<>();
        holes.add(new Hole(0,0,holeXSize,holeYSize, holeColor));
        holes.add(new Hole(canvas.getWidth() - holeXSize,  0,holeXSize,holeYSize, holeColor));
        holes.add(new Hole(0,canvas.getHeight() - holeYSize,holeXSize,holeYSize, holeColor));
        holes.add(new Hole(canvas.getWidth() - holeXSize,canvas.getHeight() - holeYSize,holeXSize,holeYSize, holeColor));
        this.holes = holes;
    }

    public int getBoardWidth()
    {
        return frame.getCanvas().getWidth();
    }

    public int getBoardHeight()
    {
        return frame.getCanvas().getHeight();
    }

    public void repaintBoard()
    {
        frame.getCanvas().repaint();
    }

    public void setFrame(BounceFrame frame) {
        this.frame = frame;
        setHoles(frame.getCanvas());
    }
}
