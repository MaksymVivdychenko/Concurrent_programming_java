import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class FoxMatrixCalculator implements IMatrixCalculator {
    private final int totalWorkers;

    public FoxMatrixCalculator(int totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    // Внутрішній клас завдання для потоку
    private static class GridWorker implements Runnable {
        private final int rowIdx;
        private final int colIdx;
        private final int gridDim;
        private final int chunkSize;

        // 4D масиви для зберігання сітки матриць
        private final int[][][][] gridA;
        private final int[][][][] stateB;
        private final int[][][][] bufferB;
        private final int[][][][] gridC;

        private final CyclicBarrier syncBarrier;

        public GridWorker(int rowIdx, int colIdx, int gridDim, int chunkSize,
                          int[][][][] gridA, int[][][][] stateB, int[][][][] bufferB,
                          int[][][][] gridC, CyclicBarrier syncBarrier) {
            this.rowIdx = rowIdx;
            this.colIdx = colIdx;
            this.gridDim = gridDim;
            this.chunkSize = chunkSize;
            this.gridA = gridA;
            this.stateB = stateB;
            this.bufferB = bufferB;
            this.gridC = gridC;
            this.syncBarrier = syncBarrier;
        }

        @Override
        public void run() {
            for (int step = 0; step < gridDim; step++) {
                // 1. Знаходимо потрібний блок A та виконуємо множення
                int sourceCol = (rowIdx + step) % gridDim;
                int[][] chunkA = gridA[rowIdx][sourceCol];
                int[][] chunkB = stateB[rowIdx][colIdx];
                int[][] chunkC = gridC[rowIdx][colIdx];

                computePartialProduct(chunkA, chunkB, chunkC);
                awaitBarrier();

                // 2. Підготовка до зсуву: читаємо блок B у сусіда знизу
                int neighborRow = (rowIdx + 1) % gridDim;
                bufferB[rowIdx][colIdx] = stateB[neighborRow][colIdx];
                awaitBarrier();

                // 3. Застосовуємо зсув (оновлюємо свій блок B)
                stateB[rowIdx][colIdx] = bufferB[rowIdx][colIdx];
                awaitBarrier();
            }
        }

        private void computePartialProduct(int[][] a, int[][] b, int[][] c) {
            for (int i = 0; i < chunkSize; i++) {
                for (int j = 0; j < chunkSize; j++) {
                    for (int k = 0; k < chunkSize; k++) {
                        c[i][j] += a[i][k] * b[k][j];
                    }
                }
            }
        }

        // Допоміжний метод, щоб уникнути дублювання try-catch
        private void awaitBarrier() {
            try {
                syncBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Thread.currentThread().interrupt(); // Відновлюємо статус переривання
                throw new RuntimeException("Помилка синхронізації потоків", ex);
            }
        }
    }

    @Override
    public int[][] MultiplyMatrix(int[][] matrixA, int[][] matrixB) {
        int size = matrixA.length;
        int[][] finalResult = new int[size][size];

        int gridDim = (int) Math.sqrt(totalWorkers);
        int chunkSize = size / gridDim;

        // Ініціалізація блокових структур
        int[][][][] chunksA = new int[gridDim][gridDim][chunkSize][chunkSize];
        int[][][][] activeB = new int[gridDim][gridDim][chunkSize][chunkSize];
        int[][][][] chunksC = new int[gridDim][gridDim][chunkSize][chunkSize];

        // Нарізка вхідних матриць на блоки
        for (int r = 0; r < gridDim; r++) {
            for (int c = 0; c < gridDim; c++) {
                for (int localR = 0; localR < chunkSize; localR++) {
                    for (int localC = 0; localC < chunkSize; localC++) {
                        int globalR = r * chunkSize + localR;
                        int globalC = c * chunkSize + localC;

                        chunksA[r][c][localR][localC] = matrixA[globalR][globalC];
                        activeB[r][c][localR][localC] = matrixB[globalR][globalC];
                        // chunksC за замовчуванням заповнюється нулями
                    }
                }
            }
        }

        // Буфер для зсуву
        int[][][][] tempB = new int[gridDim][gridDim][][];

        CyclicBarrier barrier = new CyclicBarrier(totalWorkers);
        Thread[] workerThreads = new Thread[totalWorkers];
        int threadId = 0;

        // Розподіл завдань та запуск потоків
        for (int r = 0; r < gridDim; r++) {
            for (int c = 0; c < gridDim; c++) {
                GridWorker worker = new GridWorker(r, c, gridDim, chunkSize, chunksA, activeB, tempB, chunksC, barrier);
                workerThreads[threadId] = new Thread(worker);
                workerThreads[threadId].start();
                threadId++;
            }
        }

        // Очікування завершення всіх обчислень
        for (Thread t : workerThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Збірка фінальної матриці з готових блоків
        for (int r = 0; r < gridDim; r++) {
            for (int c = 0; c < gridDim; c++) {
                for (int localR = 0; localR < chunkSize; localR++) {
                    System.arraycopy(chunksC[r][c][localR], 0, finalResult[r * chunkSize + localR], c * chunkSize, chunkSize);
                }
            }
        }

        return finalResult;
    }
}