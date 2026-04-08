import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var countOfSimulation = 100;
        var consumerTimerMs = 50;
        var simulationTimerS = 3;
        var queueCapacity = 5;
        var monitorTimerMs = 150;
        var consumersCount = 5;

        ArrayList<Future<SimulationStatistic>> statistics = new ArrayList<>();
        var simulationPool = Executors.newFixedThreadPool(countOfSimulation);
        for (int i = 0; i < countOfSimulation; i++) {
            statistics.add(simulationPool.submit(
                    new Simulation(consumersCount, consumerTimerMs, simulationTimerS, queueCapacity, monitorTimerMs)));
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

        System.out.println(String.format("%.2f", meanQueue) + " " + String.format("%.2f", rejectionProbability));
    }
}
