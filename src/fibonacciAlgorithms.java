import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.invoke.MethodType;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class fibonacciAlgorithms {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    private static int numberOfTrials = 1;
    private static int MAXINPUTSIZE  = (int) Math.pow(2, 26);
    private static int MININPUTSIZE  =  1;
//    private static int SIZEINCREMENT = 2;

    static Random rand = new Random();

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time
    private static String ResultsFolderPath = "/home/zach/Results/lab4/"; // pathname to results folder
    private static FileWriter resultsFile;
    private static PrintWriter resultsWriter;



    public static void main(String args[])
    {
        verifyCorrectFibonacciNumber();
//        runFullExperiment("slowQuickSort-ONSORTED-1-TRASH.txt");
//        runFullExperiment("slowQuickSort-ONSORTED-2.txt");
//        runFullExperiment("slowQuickSort-ONSORTED-3.txt");
    }

    private static void runFullExperiment(String resultsFileName) {
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch(Exception e) {

            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...

        }

        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial


        resultsWriter.println("#InputSize    AverageTime        DoublingRatio"); // # marks a comment in gnuplot data

        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */
//        double[] timeRatios;
        double previousTime = 0;

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize++) {

            // progress message...

            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */

            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */

            System.out.println("Collecting the trash...");
            System.gc();

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)

            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the

            // stopwatch methods themselves

            //BatchStopwatch.start(); // comment this line if timing trials individually



            // run the trials
            System.out.println("Timing Each sort individually wo gc every time forced...");
            System.out.print("    Starting trials for input size "+inputSize+" ... ");
            for (long trial = 0; trial < numberOfTrials; trial++) {


                long[] testList = createAscendingList(inputSize);

                /* force garbage collection before each trial run so it is not included in the time */
                //System.gc();

                // force presorted lists
                //quickSort(testList);
                //System.gc();

                TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                ///////////////////////////////////////////
                /*              DO BIDNESS              */
                /////////////////////////////////////////




                ///////////////////////////////////////////
                /*             END DO BIDNESS           */
                /////////////////////////////////////////

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually

            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch
            double doublingRatio = 0;
            if (previousTime > 0) {
                doublingRatio = averageTimePerTrialInBatch / previousTime;
            }

            previousTime = averageTimePerTrialInBatch;
            /* print data for this size of input */

            resultsWriter.printf("%12d  %18.2f %18.1f\n",inputSize, averageTimePerTrialInBatch, doublingRatio); // might as well make the columns look nice

            resultsWriter.flush();

            System.out.println(" ....done.");

        }
    }


    // iterative, in-place fibonacci algorithm
    static long fibLoop(int x) {
        // temp values to store in place
        long twoPrevious = 0, previous = 1, current;
        // exit if fib will be 0
        if (x == 0)
            return twoPrevious;

        // loop until we reach the target number x while continually adding up
        // the previous two numbers in the sequence and continually replacing
        // each of the values after they are used
        for (int i = 2; i <= x; i++) {
            current = twoPrevious + previous;
            twoPrevious = previous;
            previous = current;
        }
        // return the final result
        return previous;
    }



    // normal simple recursive algorithm
    static long fibRecur(long x) {
        // base case, head back up if number is 0 or 1
        if (x <= 1)
            return x;

        // continually call the function working down a recursive tree
        // when we get to the bottom, add em all up
        return fibRecur(x - 1) + fibRecur(x - 2);
    }




    // wrapper for the dynamic recursive function
    static long fibRecurDP(int x) {
        // sets up the storage for the dynnamic algo to store it's saved values
        long[] memo = new long[x + 2];
        // call the actual worker to get the fib result
        long result = fibRecurDPWorker(x, memo);
        return result;
    }

    // dynamic recursive fibonacci algorithm
    static long fibRecurDPWorker(int x, long[] memo) {
        memo = new long[x + 2]; // 1 extra to handle case, n = 0

        // exits if the array position has not been written to
        if (memo[x] == -1)
            return memo[x];

        // case to get out if we are less than 2
        if (x <= 2)
            return 1;

        // recursive calls to the algo to add the results
        long res = fibRecurDPWorker(x - 1, memo) + fibRecurDPWorker(x - 2, memo);

        // store added result in the array for the future, where it is passed recursively to the
        // function
        memo[x] = res;

        // return the result to be used in the next recursive call
        return res;
    }




    // algorithm using the matrix of {{1, 1} {1, 0}} taken to the power of
    // the fib number we want
    static long fibMatrix(long x) {
        // create new matrix of {{1, 1} {1, 0}}
        long F[][] = new long[][] { { 1, 1 }, { 1, 0 } };
        // get out if x == 0
        if (x == 0)
            return 0;
        // the function to put the matrix to the power of x
        power(F, x - 1);
        // return the result
        return F[0][0];
    }

    /*
      Helper function that multiplies 2 matrices F and M of size 2*2, and puts the
      multiplication result back to F[][]
      This is what does our matrix multiplication for us
     */
    static void multiply(long F[][], long M[][]) {
        // do the multiplication
        long x = F[0][0] * M[0][0] + F[0][1] * M[1][0];
        long y = F[0][0] * M[0][1] + F[0][1] * M[1][1];
        long z = F[1][0] * M[0][0] + F[1][1] * M[1][0];
        long w = F[1][0] * M[0][1] + F[1][1] * M[1][1];

        // repopulate the matrix
        F[0][0] = x;
        F[0][1] = y;
        F[1][0] = z;
        F[1][1] = w;
    }

    /*
      Helper function that calculates F[][] raise to the power n and puts the
      result in F[][]
     */
    static void power(long F[][], long x) {
        long i;
        // new matrix of the same value to use for our multiplication
        long M[][] = new long[][] { { 1, 1 }, { 1, 0 } };

        // x - 1 times multiply the matrix to {{1,0},{0,1}}
        // multiplies it all of the times necessary
        for (i = 2; i <= x; i++)
            multiply(F, M); // the function that actually does the matrix multiplication
    }



    


    private static void verifyCorrectFibonacciNumber() {
        int expected = 4181;
        long result = fibMatrix(19);
        System.out.println("Fib number is: " + result);
        if (result == expected) {
            System.out.println("Fib number is correct");
        } else {
            System.out.println("BROKEN !!!!!!");
        }
    }


    //<editor-fold desc="Utilities">
    /* UTILITY FUNCTIONS */
    /* A utility function to print array of size n */
    private static void printArray(long arr[]) {
        int n = arr.length;
        for (long l : arr) System.out.print(l + " ");
        System.out.println();
    }

    private static long[] createRandomIntegerList(int size) {
        long[] newList = new long[size];
        for (int j = 0; j < size; j++) {
            newList[j] = new Random().nextLong();
        }

        return newList;
    }
    public static long[] createAscendingList(int size) {
        long[] newList = new long[size];
        long listValue = new Random().nextInt(10);
        for (int j = 0; j < size; j++) {
            newList[j] = listValue;
            listValue = listValue + new Random().nextInt(10);
        }
        return newList;
    }

    // util function to calculate n based on x
    public static long log2(long x) {
        return (long) (Math.log(x) / Math.log(2));
    }
    //</editor-fold>
}
