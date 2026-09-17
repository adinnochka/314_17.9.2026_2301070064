import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class SynchronizationTrap {

    static final long TOTAL_POINTS = 50_000_000;
    static final int THREADS = 4;

    public static void main(String[] args) throws InterruptedException {

        // AtomicLong version with 4 threads
        AtomicLong totalHits = new AtomicLong(0);

        Thread[] threads = new Thread[THREADS];
        long pointsPerThread = TOTAL_POINTS / THREADS;

        long startAtomic = System.nanoTime();

        for (int i = 0; i < THREADS; i++) {

            threads[i] = new Thread(() -> {

                for (long j = 0; j < pointsPerThread; j++) {

                    double x = ThreadLocalRandom.current().nextDouble();
                    double y = ThreadLocalRandom.current().nextDouble();

                    if (x * x + y * y <= 1.0) {
                        totalHits.incrementAndGet();
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endAtomic = System.nanoTime();

        double piAtomic = 4.0 * totalHits.get() / TOTAL_POINTS;
        double atomicTimeMs = (endAtomic - startAtomic) / 1_000_000.0;


        // Single-thread version
        long singleHits = 0;

        long startSingle = System.nanoTime();

        for (long i = 0; i < TOTAL_POINTS; i++) {

            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();

            if (x * x + y * y <= 1.0) {
                singleHits++;
            }
        }

        long endSingle = System.nanoTime();

        double piSingle = 4.0 * singleHits / TOTAL_POINTS;
        double singleTimeMs = (endSingle - startSingle) / 1_000_000.0;


        System.out.println("=== AtomicLong (4 threads) ===");
        System.out.println("Total hits: " + totalHits.get());
        System.out.println("Approximate PI: " + piAtomic);
        System.out.println("Runtime: " + atomicTimeMs + " ms");

        System.out.println();

        System.out.println("=== Single Thread ===");
        System.out.println("Total hits: " + singleHits);
        System.out.println("Approximate PI: " + piSingle);
        System.out.println("Runtime: " + singleTimeMs + " ms");
    }
}