import java.util.Random;

public class GaussingDistribution {
    private final double mean;
    private final double sd;
    private final Random rand = new Random();

    public GaussingDistribution(double mean, double sd)
    {
        this.mean = mean;
        this.sd = sd;
    }

    public int nextGaussian()
    {
        return (int)(rand.nextGaussian() * sd + mean);
    }
}
