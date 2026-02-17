import javax.swing.*;
import java.awt.*;

public class BounceFrame extends JFrame {
    private BilliardBoard board;
    private final BallCanvas canvas;
    private final JLabel scoreLabel;
    private final JButton buttonStart;
    private final JButton buttonStop;
    private final JButton buttonPriorityTest;
    private final JButton buttonJoinTest;

    private final JPanel buttonPanel;

    public BounceFrame(int width, int height)
    {
        this.setTitle("Bounce program");
        this.canvas = new BallCanvas(this);
        canvas.setPreferredSize(new Dimension(width, height));

        System.out.println("In Frame Thread name = " + Thread.currentThread().getName());
        Container content = this.getContentPane();
        content.add(canvas, BorderLayout.CENTER);

        this.scoreLabel = new JLabel("Dropped balls: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(scoreLabel, BorderLayout.NORTH);

        this.buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);

        this.buttonStart = new JButton("1000 balls");
        buttonStart.setFont(new Font("Arial", Font.BOLD, 30));
        this.buttonStop = new JButton("Stop");
        buttonStop.setFont(new Font("Arial", Font.BOLD, 30));
        this.buttonPriorityTest = new JButton("Priority test");
        buttonPriorityTest.setFont(new Font("Arial", Font.BOLD, 30));
        this.buttonJoinTest = new JButton("Join test");
        buttonJoinTest.setFont(new Font("Arial", Font.BOLD, 30));


        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonStop);
        buttonPanel.add(buttonPriorityTest);
        buttonPanel.add(buttonJoinTest);

        content.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(true);
    }

    public void setBoard(BilliardBoard board) {
        this.board = board;
        buttonStart.addActionListener(e -> board.startBall());
        buttonStop.addActionListener(e -> board.stopGame());
        buttonPriorityTest.addActionListener(e -> board.priorityTest());
        buttonJoinTest.addActionListener(e -> board.joinTest());
    }

    public BallCanvas getCanvas() {
        return canvas;
    }

    public BilliardBoard getBoard() {
        return board;
    }

    public void updateScorePanel(int counter)
    {
        scoreLabel.setText("Dropped balls: " + counter);
    }
}
