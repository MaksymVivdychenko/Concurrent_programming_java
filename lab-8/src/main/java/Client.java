import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    public Client(String host, int port) throws IOException {
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
        int[][] C = receiveMatrix(n, p);

        long totalTime = System.nanoTime() - startTotal;

        System.out.println("Total time: " + (totalTime / 1e6) + " ms");

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

    private int[][] receiveMatrix(int rows, int cols) throws IOException {
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

    public void disconnect() throws IOException {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int mode = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        int n = args.length > 3 ? Integer.parseInt(args[3]) : 500;
        int m = args.length > 4 ? Integer.parseInt(args[4]) : 500;
        int p = args.length > 5 ? Integer.parseInt(args[5]) : 500;
        Client client = new Client(host, port);
        client.executeTask(mode, n, m, p);
        client.disconnect();
    }
}