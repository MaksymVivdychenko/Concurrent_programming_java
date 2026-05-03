#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MASTER_RANK 0
#define N 15

#define MIN_MATRIX_VALUE 0
#define MAX_MATRIX_VALUE 9

void generate_matrix(int* matrix, int size, int min, int max) {
    for (int i = 0; i < size * size; i++) {
        matrix[i] = (rand() % (max - min + 1)) + min;
    }
}

void print_array(int* array, int size) {
    for (int i = 0; i < size; i++) {
        printf("%d\t", array[i]);
    }
    printf("\n");
}

void print_matrix(int* matrix, int size) {
    for (int i = 0; i < size; i++) {
        print_array(&matrix[i * size], size);
    }
}

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank, tasksCount;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &tasksCount);

    if (tasksCount < 2) {
        if (rank == MASTER_RANK) {
            printf("Need at least 2 tasks!\n");
        }
        MPI_Abort(MPI_COMM_WORLD, 1);
        return 1;
    }

    srand(time(NULL) + rank);

    int *matrix_a = NULL;
    int *matrix_b = NULL;
    int *result_c = NULL;

    if (rank == MASTER_RANK) {
        matrix_a = (int*)malloc(N * N * sizeof(int));
        matrix_b = (int*)malloc(N * N * sizeof(int));
        result_c = (int*)malloc(N * sizeof(int));

        generate_matrix(matrix_a, N, MIN_MATRIX_VALUE, MAX_MATRIX_VALUE);
        generate_matrix(matrix_b, N, MIN_MATRIX_VALUE, MAX_MATRIX_VALUE);
    }

    int base_rows = N / tasksCount;
    int extra_rows = N % tasksCount;

    int *rows_per_proc = (int*)malloc(tasksCount * sizeof(int));
    int *sendcounts_elements = (int*)malloc(tasksCount * sizeof(int));
    int *displs_elements = (int*)malloc(tasksCount * sizeof(int));
    int *displs_rows = (int*)malloc(tasksCount * sizeof(int));

    int current_row_offset = 0;
    for (int i = 0; i < tasksCount; i++) {
        rows_per_proc[i] = base_rows + (i < extra_rows ? 1 : 0);
        
        sendcounts_elements[i] = rows_per_proc[i] * N;
        displs_elements[i] = current_row_offset * N;
        
        displs_rows[i] = current_row_offset;
        current_row_offset += rows_per_proc[i];
    }

    int local_rows = rows_per_proc[rank];
    int local_elements = sendcounts_elements[rank];

    int *local_a = (int*)malloc(local_elements * sizeof(int));
    int *local_b = (int*)malloc(local_elements * sizeof(int));
    int *local_c = (int*)malloc(local_rows * sizeof(int));

    MPI_Scatterv(matrix_a, sendcounts_elements, displs_elements, MPI_INT, 
                 local_a, local_elements, MPI_INT, MASTER_RANK, MPI_COMM_WORLD);
                 
    MPI_Scatterv(matrix_b, sendcounts_elements, displs_elements, MPI_INT, 
                 local_b, local_elements, MPI_INT, MASTER_RANK, MPI_COMM_WORLD);

    for (int i = 0; i < local_rows; i++) {
        int sum_a = 0;
        int sum_b = 0;
        
        for (int j = 0; j < N; j++) {
            sum_a += local_a[i * N + j];
            sum_b += local_b[i * N + j];
        }
        
        int avg_a = sum_a / N;
        int avg_b = sum_b / N;

        local_c[i] = avg_a * avg_b;
    }

    MPI_Gatherv(local_c, local_rows, MPI_INT, 
                result_c, rows_per_proc, displs_rows, MPI_INT, 
                MASTER_RANK, MPI_COMM_WORLD);

    if (rank == MASTER_RANK) {
        printf("Matrix A:\n");
        print_matrix(matrix_a, N);
        
        printf("Matrix B:\n");
        print_matrix(matrix_b, N);
        
        printf("Result:\n");
        print_array(result_c, N);
    }

    if (rank == MASTER_RANK) {
        free(matrix_a);
        free(matrix_b);
        free(result_c);
    }
    free(rows_per_proc);
    free(sendcounts_elements);
    free(displs_elements);
    free(displs_rows);
    free(local_a);
    free(local_b);
    free(local_c);

    MPI_Finalize();
    return 0;
}