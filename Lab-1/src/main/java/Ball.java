import java.awt.*;

public class Ball extends BaseObject {
    private int dx = 2 * 4;
    private int dy = 2 * 4;

    public Ball(int x, int y, int xSize, int ySize, Color color) {
        super(x, y, xSize, ySize, color);
    }


    public void move(int width, int height)
    {
        x += dx;
        y += dy;
        if(x < 0)
        {
            x = 0; dx = -dx;
        }
        if(x + xSize >= width)
        {
            x = width - xSize; dx = -dx;
        }
        if(y < 0)
        {
            y = 0; dy = -dy;
        }
        if(y + ySize >= height)
        {
            y = height - ySize; dy = -dy;
        }
    }
}
