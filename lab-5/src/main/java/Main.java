import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var countOfSimulation = 10;
        var consumerTimerMs = 50;
        var simulationTimerS = 3;
        var queueCapacity = 5;
        var monitorTimerMs = 150;
        var consumersCount = 5;
        var producerTimeMeanMs = 10;
        var producerTimeSdMs = 2;

        ArrayList<Future<SimulationStatistic>> statistics = new ArrayList<>();
        var simulationPool = Executors.newFixedThreadPool(countOfSimulation);
        for (int i = 0; i < countOfSimulation; i++) {
            statistics.add(simulationPool.submit(new Simulation(consumersCount, consumerTimerMs, simulationTimerS, queueCapacity, monitorTimerMs, i, producerTimeMeanMs, producerTimeSdMs)));
        }
        simulationPool.shutdown();
        double meanQueue = 0;
        double rejectionProbability = 0;
        for (var simResult : statistics) {
            meanQueue += simResult.get().meanQueue;
            rejectionProbability += simResult.get().rejectionProbability;
        }

        meanQueue /= countOfSimulation;
        rejectionProbability /= countOfSimulation;
        System.out.println("\nTime of simulation: " + simulationTimerS + " Count of simulations: " + countOfSimulation);
        System.out.println("Testing data: consumer task time ms: " + consumerTimerMs + "; count of consumers: " + consumersCount + "; producer time mean ms: " + producerTimeMeanMs + "; producer time sd ms: " + producerTimeSdMs);
        System.out.println("Avg length of queue: " + String.format("%.2f", meanQueue) + "; Probability of failure: " + String.format("%.4f", rejectionProbability));
    }
}
