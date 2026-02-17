import javax.swing.*;

public class Bounce {
    public static void main(String[] args)
    {
        BilliardBoard board = new BilliardBoard();
        BounceFrame frame = new BounceFrame(450*4, 350*3);
        frame.setBoard(board);
        board.setFrame(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("Thread name = " + Thread.currentThread().getName());
    }
}
