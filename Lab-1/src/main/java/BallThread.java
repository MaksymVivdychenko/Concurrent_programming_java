import java.awt.*;

public class BallThread extends Thread {
    private Ball b;
    private final BilliardBoard board;

    public BallThread(Ball ball, Component canvas, BilliardBoard board)
    {
        b = ball;
        this.board = board;
    }

    @Override
    public void run()
    {
        try {
            for(int i = 0; i < 10000; i++)
            {
                b.move(board.getBoardWidth(), board.getBoardHeight());
                if(board.isInHole(b))
                {
                    board.removeBall(b);
                    board.repaintBoard();
                    this.interrupt();
                    return;
                }
                board.repaintBoard();
                //System.out.println("Thread ball = " + Thread.currentThread().getName());
                Thread.sleep(5);

            }
        }catch(InterruptedException _) {}
    }
}
