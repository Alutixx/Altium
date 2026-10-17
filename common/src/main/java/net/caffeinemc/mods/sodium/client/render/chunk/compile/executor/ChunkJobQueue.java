package net.caffeinemc.mods.sodium.client.render.chunk.compile.executor;

import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Experimental two-tier queue for low-core-count scheduling.
 *
 * <p>Important jobs are preferred, but a bounded priority burst prevents a continuous
 * stream of important work from starving normal jobs. The queue deliberately reuses
 * the existing important/non-important signal so task lifecycle and callers remain
 * unchanged while this prototype is evaluated.</p>
 */
class ChunkJobQueue {
    private static final int MAX_IMPORTANT_BURST = 8;

    private final ConcurrentLinkedDeque<ChunkJob> importantJobs = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<ChunkJob> normalJobs = new ConcurrentLinkedDeque<>();

    private final AtomicLong jobDurationSum = new AtomicLong();
    private final Semaphore semaphore = new Semaphore(0);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    // Accessed only from synchronized getNextTask().
    private int consecutiveImportantJobs;

    public boolean isRunning() {
        return this.isRunning.get();
    }

    public void add(ChunkJob job, boolean important) {
        Validate.isTrue(this.isRunning(), "Queue is no longer running");

        if (important) {
            this.importantJobs.addFirst(job);
        } else {
            this.normalJobs.addLast(job);
        }

        this.jobDurationSum.addAndGet(job.getEstimatedDuration());
        this.semaphore.release(1);
    }

    @Nullable
    public ChunkJob waitForNextJob() throws InterruptedException {
        if (!this.isRunning()) {
            return null;
        }

        this.semaphore.acquire();

        var job = this.getNextTask();
        if (job != null) {
            this.jobDurationSum.addAndGet(-job.getEstimatedDuration());
        }
        return job;
    }

    public boolean stealJob(ChunkJob job) {
        if (!this.semaphore.tryAcquire()) {
            return false;
        }

        var success = this.importantJobs.remove(job) || this.normalJobs.remove(job);

        if (success) {
            this.jobDurationSum.addAndGet(-job.getEstimatedDuration());
        } else {
            // Restore the permit if the target job was already consumed by a worker.
            this.semaphore.release(1);
        }

        return success;
    }

    @Nullable
    private synchronized ChunkJob getNextTask() {
        // Give normal work a turn after a bounded burst, but never leave workers idle
        // when the normal queue is empty.
        if (this.consecutiveImportantJobs >= MAX_IMPORTANT_BURST) {
            var normal = this.normalJobs.pollFirst();
            if (normal != null) {
                this.consecutiveImportantJobs = 0;
                return normal;
            }
        }

        var important = this.importantJobs.pollFirst();
        if (important != null) {
            this.consecutiveImportantJobs++;
            return important;
        }

        var normal = this.normalJobs.pollFirst();
        if (normal != null) {
            this.consecutiveImportantJobs = 0;
        }
        return normal;
    }

    public Collection<ChunkJob> shutdown() {
        var list = new ArrayDeque<ChunkJob>();

        this.isRunning.set(false);

        while (this.semaphore.tryAcquire()) {
            var task = this.importantJobs.pollFirst();
            if (task == null) {
                task = this.normalJobs.pollFirst();
            }

            if (task != null) {
                list.add(task);
            }
        }

        // Force worker threads to wake up and exit.
        this.semaphore.release(Runtime.getRuntime().availableProcessors());
        this.jobDurationSum.set(0);

        return list;
    }

    public int size() {
        return this.semaphore.availablePermits();
    }

    public long getJobDurationSum() {
        return this.jobDurationSum.get();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }
}
