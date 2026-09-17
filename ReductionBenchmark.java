import java.util.concurrent.ThreadLocalRandom;

public class ReductionBenchmark {

    static final long TOTAL_POINTS = 100_000_000;

    public static void main(String[] args) throws InterruptedException {

        int[] threadCounts = {1, 2, 4, 8, 16, 32};
        double baselineTime = 0;

        System.out.println("Threads | Runtime(ms) | PI | Speedup | Efficiency");
        System.out.println("--------------------------------------------------");

        for (int threadCount : threadCounts) {

            long[] partialHits = new long[threadCount];
            Thread[] threads = new Thread[threadCount];

            long pointsPerThread = TOTAL_POINTS / threadCount;

            long startTime = System.nanoTime();

            for (int i = 0; i < threadCount; i++) {

                final int threadId = i;

                threads[i] = new Thread(() -> {

                    long localHits = 0;

                    for (long j = 0; j < pointsPerThread; j++) {

                        double x = ThreadLocalRandom.current().nextDouble();
                        double y = ThreadLocalRandom.current().nextDouble();

                        if (x * x + y * y <= 1.0) {
                            localHits++;
                        }
                    }

                    partialHits[threadId] = localHits;
                });

                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            long totalHits = 0;

            for (long hits : partialHits) {
                totalHits += hits;
            }

            long endTime = System.nanoTime();

            double runtimeMs =
                    (endTime - startTime) / 1_000_000.0;

            double pi =
                    4.0 * totalHits / TOTAL_POINTS;

            if (threadCount == 1) {
                baselineTime = runtimeMs;
            }

            double speedup =
                    baselineTime / runtimeMs;

            double efficiency =
                    (speedup / threadCount) * 100.0;

            System.out.printf(
                    "%d | %.2f | %.8f | %.2fx | %.2f%%%n",
                    threadCount,
                    runtimeMs,
                    pi,
                    speedup,
                    efficiency
            );
        }
    }
}