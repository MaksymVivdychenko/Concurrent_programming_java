#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MASTER_RANK 0
#define N 150
#define STRING_LEN 5
#define MAX_STR_LEN (STRING_LEN + 1) // +1 для '\0'

void generate_strings(char* array, int size, int length) {
    const char alphabet[] = "abcdefghijklmnopqrstuvwxyz";
    int alphabet_len = strlen(alphabet);

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < length; j++) {
            int randomIndex = rand() % alphabet_len;
            array[i * MAX_STR_LEN + j] = alphabet[randomIndex];
        }
        array[i * MAX_STR_LEN + length] = '\0';
    }
}

void print_array(const char* array, int size) {
    for (int i = 0; i < size; i++) {
        printf("%s\t", &array[i * MAX_STR_LEN]);
    }
    printf("\n");
}

int compare_strings(const void* a, const void* b) {
    return strncmp((const char*)a, (const char*)b, MAX_STR_LEN);
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

    char* strings = NULL;

    if (rank == MASTER_RANK) {
        strings = (char*)malloc(N * MAX_STR_LEN * sizeof(char));
        generate_strings(strings, N, STRING_LEN);
    }

    int base_strings = N / tasksCount;
    int extra_strings = N % tasksCount;

    int* sendcounts = (int*)malloc(tasksCount * sizeof(int));
    int* displs = (int*)malloc(tasksCount * sizeof(int));

    int current_offset = 0;
    for (int i = 0; i < tasksCount; i++) {
        int task_elements = base_strings + (i < extra_strings ? 1 : 0);
        
        sendcounts[i] = task_elements * MAX_STR_LEN; 
        displs[i] = current_offset * MAX_STR_LEN;
        
        current_offset += task_elements;
    }

    int local_el_count = sendcounts[rank] / MAX_STR_LEN;
    char* local_string = (char*)malloc(sendcounts[rank] * sizeof(char));

    MPI_Scatterv(strings, sendcounts, displs, MPI_CHAR, 
                 local_string, sendcounts[rank], MPI_CHAR, 
                 MASTER_RANK, MPI_COMM_WORLD);

    if (local_el_count > 0) {
        qsort(local_string, local_el_count, MAX_STR_LEN, compare_strings);
    }

    char first_el[MAX_STR_LEN] = {0};
    
    if (local_el_count > 0) {
        strncpy(first_el, local_string, MAX_STR_LEN);
    }

    char* gathered_first_els = NULL;
    if (rank == MASTER_RANK) {
        gathered_first_els = (char*)malloc(tasksCount * MAX_STR_LEN * sizeof(char));
    }

    MPI_Gather(first_el, MAX_STR_LEN, MPI_CHAR, 
               gathered_first_els, MAX_STR_LEN, MPI_CHAR, 
               MASTER_RANK, MPI_COMM_WORLD);

    if (rank == MASTER_RANK) {
        printf("Strings per task: %d\n", base_strings);
        printf("Extra: %d\n", extra_strings);
        printf("Original array (%d elements):\n", N);
        print_array(strings, N);
        printf("First elements after local sorts:\n");
        print_array(gathered_first_els, tasksCount);

        free(strings);
        free(gathered_first_els);
    }

    free(sendcounts);
    free(displs);
    free(local_string);

    MPI_Finalize();
    return 0;
}