#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void init_matrix(double *mat, int rows, int cols) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            mat[i * cols + j] = rand() % 10 + 1;
        }
    }
}

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int N = atoi(argv[1]);
    int iterations = atoi(argv[2]);

    int rows_per_proc = N / size;
    int extra = N % size;
    int my_rows = rows_per_proc + (rank < extra ? 1 : 0);

    double *A = NULL, *B = NULL, *C = NULL;
    double *my_A = (double *)malloc(my_rows * N * sizeof(double));
    double *local_B = (double *)malloc(N * N * sizeof(double));
    double *my_C = (double *)malloc(my_rows * N * sizeof(double));

    if (rank == 0) {
        srand(time(NULL));
        A = (double *)malloc(N * N * sizeof(double));
        B = (double *)malloc(N * N * sizeof(double));
        C = (double *)malloc(N * N * sizeof(double));
        init_matrix(A, N, N);
        init_matrix(B, N, N);
    }

    double total_time = 0.0;

    for (int iter = 0; iter < iterations; iter++) {
        MPI_Barrier(MPI_COMM_WORLD);
        double start_time = MPI_Wtime();

        if (rank == 0) {
            int offset = my_rows;
            for (int i = 1; i < size; i++) {
                int proc_rows = rows_per_proc + (i < extra ? 1 : 0);
                MPI_Send(&A[offset * N], proc_rows * N, MPI_DOUBLE, i, 0, MPI_COMM_WORLD);
                MPI_Send(B, N * N, MPI_DOUBLE, i, 2, MPI_COMM_WORLD);
                offset += proc_rows;
            }

            for (int i = 0; i < my_rows; i++) {
                for (int j = 0; j < N; j++) {
                    C[i * N + j] = 0.0;
                    for (int k = 0; k < N; k++) {
                        C[i * N + j] += A[i * N + k] * B[k * N + j];
                    }
                }
            }

            offset = my_rows;
            for (int i = 1; i < size; i++) {
                int proc_rows = rows_per_proc + (i < extra ? 1 : 0);
                MPI_Recv(&C[offset * N], proc_rows * N, MPI_DOUBLE, i, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
                offset += proc_rows;
            }
        } else {
            MPI_Recv(my_A, my_rows * N, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            MPI_Recv(local_B, N * N, MPI_DOUBLE, 0, 2, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

            for (int i = 0; i < my_rows; i++) {
                for (int j = 0; j < N; j++) {
                    my_C[i * N + j] = 0.0;
                    for (int k = 0; k < N; k++) {
                        my_C[i * N + j] += my_A[i * N + k] * local_B[k * N + j];
                    }
                }
            }
            MPI_Send(my_C, my_rows * N, MPI_DOUBLE, 0, 1, MPI_COMM_WORLD);
        }

        double end_time = MPI_Wtime();
        total_time += (end_time - start_time);
    }

    if (rank == 0) {
        printf("%d, %d, %f\n", N, size, total_time / iterations);
        free(A); free(B); free(C);
    }

    free(my_A); free(local_B); free(my_C);
    MPI_Finalize();
    return 0;
}