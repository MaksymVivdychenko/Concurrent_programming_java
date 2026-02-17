import java.awt.*;

public abstract class BaseObject {
    protected int x;
    public int getX() {return x;}
    protected int y;
    public int getY() {return y;}
    protected int xSize;
    public int getXSize() {return xSize;}
    protected int ySize;
    private final Color color;
    public Color getColor() {return color;}

    public int getYSize() {return ySize;}

    public BaseObject(int x, int y, int xSize, int ySize, Color color)
    {
        this.x = x;
        this.y = y;
        this.xSize = xSize;
        this.ySize = ySize;
        this.color = color;
    }
}

