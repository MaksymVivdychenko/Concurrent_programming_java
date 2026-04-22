import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket serverSocket;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port);

        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
                var out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                var in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        ) {
            int[][] A = null, B = null;

            var mode = in.readInt();
            var n = in.readInt();
            var m = in.readInt();
            var p = in.readInt();

            if(mode == 0)
            {
                A = receiveMatrix(n, m, in);
                B = receiveMatrix(m, p, in);
            }
            else {
                A = generateMatrix(n, m);
                B = generateMatrix(m, p);
            }

            var C = parallelMultiply(A, B);

            sendMatrix(C, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] parallelMultiply(int[][] A, int[][] B) {
        int n = A.length;
        int p = B.length;
        int m = B[0].length;
        int[][] C = new int[n][m];

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        for (int i = 0; i < n; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < m; j++) {
                    int sum = 0;
                    for (int k = 0; k < p; k++) {
                        sum += A[row][k] * B[k][j];
                    }
                    C[row][j] = sum;
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                System.err.println("Computation took too long");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return C;
    }

    private void sendMatrix(int[][] matrix, DataOutputStream out) throws IOException {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                out.writeInt(matrix[i][j]);
            }
        }

        out.flush();
    }

    private int[][] receiveMatrix(int rows, int cols, DataInputStream in) throws IOException {
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = in.readInt();
            }
        }

        return result;
    }

    private int[][] generateMatrix(int n, int m) throws IOException {
        Random random = new Random();
        int[][] A = new int[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                A[i][j] = random.nextInt(10);
        return A;
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        new Server(port).start();
    }
}