import java.io.*;
import java.net.*;
import java.util.Random;

public class MatrixBenchmarker {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public MatrixBenchmarker(String host, int port) throws IOException {
        socket = new Socket(host, port);
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public long executeTask(int mode, int n, int m, int p) throws IOException {
        int[][] A = null;
        int[][] B = null;

        if (mode == 0) {
            A = generateMatrix(n, m);
            B = generateMatrix(m, p);
        }

        long startTotal = System.nanoTime();
        out.writeInt(mode);
        out.writeInt(n);
        out.writeInt(m);
        out.writeInt(p);
        out.flush();

        if (mode == 0) {
            sendMatrix(A);
            sendMatrix(B);
        }

        receiveMatrix(n, p);

        long totalTime = System.nanoTime() - startTotal;
        return totalTime;
    }

    private void sendMatrix(int[][] matrix) throws IOException {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                out.writeInt(matrix[i][j]);
            }
        }
        out.flush();
    }

    private void receiveMatrix(int rows, int cols) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                in.readInt();
            }
        }
    }

    private int[][] generateMatrix(int n, int m) {
        Random random = new Random();
        int[][] A = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                A[i][j] = random.nextInt(10);
        return A;
    }

    public void disconnect() throws IOException {
        if (socket != null) socket.close();
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        int iterations = 3;

        System.out.println("=== Починаємо тестування ===");
        System.out.printf("%-10s | %-10s | %-20s%n", "Режим", "Розмір", "Сер. час (мс)");
        System.out.println("----------------------------------------------------------");

        for (int mode = 0; mode <= 1; mode++) {
            for (int size = 500; size <= 2500; size += 500) {
                long totalNanoseconds = 0;

                for (int i = 0; i < iterations; i++) {
                    try {
                        MatrixBenchmarker client = new MatrixBenchmarker(host, port);
                        totalNanoseconds += client.executeTask(mode, size, size, size);
                        client.disconnect();
                    } catch (IOException e) {
                        System.err.println("Помилка під час тесту: " + e.getMessage());
                    }
                }

                double averageMs = (totalNanoseconds / (double) iterations) / 1e6;
                System.out.printf("%-10d | %-10d | %-20.2f%n", mode, size, averageMs);
            }
            System.out.println("----------------------------------------------------------");
        }
    }
}