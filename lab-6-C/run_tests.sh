#!/bin/bash

SOURCE="collective_MPI.c"
EXEC="collective_matrix_mult"
OUT_FILE="results_collective.csv"
ITERATIONS=20

mpicc $SOURCE -o $EXEC

echo "N,Processes,ExecutionTime" > $OUT_FILE

SIZES=(500 1000 1500 2000 2500 3000)
PROCESS_COUNTS=(1 2 4 6 8 9)

for n in "${SIZES[@]}"; do
    for p in "${PROCESS_COUNTS[@]}"; do
        echo "Testing N=$n, P=$p ($ITERATIONS internal runs)..."
        mpirun --use-hwthread-cpus --oversubscribe -np $p ./$EXEC $n $ITERATIONS >> $OUT_FILE
    done
done