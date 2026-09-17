import java.util.concurrent.ThreadLocalRandom;

public class PhantomBug {

    static long totalHits = 0;

    static final long TOTAL_POINTS = 50_000_000;
    static final int THREADS = 4;

    public static void main(String[] args) throws InterruptedException {

        Thread[] threads = new Thread[THREADS];
        long pointsPerThread = TOTAL_POINTS / THREADS;

        for (int i = 0; i < THREADS; i++) {

            threads[i] = new Thread(() -> {

                for (long j = 0; j < pointsPerThread; j++) {

                    double x = ThreadLocalRandom.current().nextDouble();
                    double y = ThreadLocalRandom.current().nextDouble();

                    if (x * x + y * y <= 1.0) {
                        totalHits++;
                    }
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        double pi = 4.0 * totalHits / TOTAL_POINTS;

        System.out.println("Total hits: " + totalHits);
        System.out.println("Approximate PI: " + pi);
    }
}