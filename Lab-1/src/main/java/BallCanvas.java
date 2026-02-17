import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BallCanvas extends JPanel {
    private BounceFrame frame;

    public BallCanvas(BounceFrame frame) {
        this.frame = frame;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 =(Graphics2D)g;
        for(Ball b : frame.getBoard().getBalls())
        {
            drawBall(g2, b);
        }
        for(Hole h : frame.getBoard().getHoles())
        {
            drawBall(g2, h);
        }
    }

    private void drawBall(Graphics2D g2, BaseObject object)
    {
        g2.setColor(object.getColor());
        g2.fill(new Ellipse2D.Double(object.x, object.y, object.xSize, object.ySize));
    }
}
