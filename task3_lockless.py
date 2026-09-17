import threading
import time

TOTAL_OPS = 2_000_000
NUM_THREADS = 4

def lockless_bench():
    ops_per_thread = TOTAL_OPS // NUM_THREADS

    # Each thread has its own result slot
    partial_results = [0] * NUM_THREADS

    def work(thread_id):
        local_sum = 0

        for _ in range(ops_per_thread):
            local_sum += 1

        partial_results[thread_id] = local_sum

    threads = [
        threading.Thread(target=work, args=(i,))
        for i in range(NUM_THREADS)
    ]

    start = time.perf_counter()

    for t in threads:
        t.start()

    for t in threads:
        t.join()

    final_result = sum(partial_results)
    elapsed = time.perf_counter() - start

    return final_result, elapsed


if __name__ == "__main__":
    result, elapsed = lockless_bench()

    locked_time = 0.7405

    print(f"Lockless: Value = {result:,} / {TOTAL_OPS:,} | Time: {elapsed:.4f}s")
    print(f"Speedup over LockedCounter: {locked_time / elapsed:.2f}x")