package io.goodforgod.aws.lambda.simple.reactive;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copy of JDK internal jdk.internal.net.http.common.SequentialScheduler
 *
 * @author Anton Kurako (GoodforGod)
 * @since 10.10.2021
 */
final class SequentialScheduler {

    /**
     * An interface to signal the completion of a {@link SequentialScheduler.RestartableTask}.
     * <p>
     * The invocation of {@code complete} completes the task. The invocation of {@code complete} may
     * restart the task, if an attempt has previously been made to run the task while it was already
     * running.
     *
     * @apiNote {@code DeferredCompleter} is useful when a task is not necessary complete when its
     *              {@code run} method returns, but will complete at a later time, and maybe in
     *              different
     *              thread. This type exists for readability purposes at use-sites only.
     */
    public abstract static class DeferredCompleter {

        /** Extensible from this (outer) class ONLY. */
        private DeferredCompleter() {}

        /** Completes the task. Must be called once, and once only. */
        public abstract void complete();
    }

    /**
     * A restartable task.
     */
    @FunctionalInterface
    public interface RestartableTask {

        /**
         * The body of the task.
         *
         * @param taskCompleter A completer that must be invoked once, and only once, when this task is
         *                      logically finished
         */
        void run(SequentialScheduler.DeferredCompleter taskCompleter);
    }

    /**
     * A simple and self-contained task that completes once its {@code run} method returns.
     */
    public abstract static class CompleteRestartableTask implements SequentialScheduler.RestartableTask {

        @Override
        public final void run(SequentialScheduler.DeferredCompleter taskCompleter) {
            try {
                run();
            } finally {
                taskCompleter.complete();
            }
        }

        /** The body of the task. */
        protected abstract void run();
    }

    /**
     * A task that runs its main loop within a synchronized block to provide memory visibility between
     * runs. Since the main loop can't run concurrently, the lock shouldn't be contended and no deadlock
     * should ever be possible.
     */
    public static final class SynchronizedRestartableTask extends SequentialScheduler.CompleteRestartableTask {

        private final Runnable mainLoop;
        private final Object lock = new Object();

        public SynchronizedRestartableTask(Runnable mainLoop) {
            this.mainLoop = mainLoop;
        }

        @Override
        protected void run() {
            synchronized (lock) {
                mainLoop.run();
            }
        }
    }

    private static final int OFFLOAD = 1;
    private static final int AGAIN = 2;
    private static final int BEGIN = 4;
    private static final int STOP = 8;
    private static final int END = 16;

    private final AtomicInteger state = new AtomicInteger(END);
    private final SequentialScheduler.RestartableTask restartableTask;
    private final SequentialScheduler.DeferredCompleter completer;
    private final SequentialScheduler.SchedulableTask schedulableTask;

    /**
     * An auxiliary task that starts the restartable task: {@code restartableTask.run(completer)}.
     */
    private final class SchedulableTask implements Runnable {

        @Override
        public void run() {
            restartableTask.run(completer);
        }
    }

    public SequentialScheduler(SequentialScheduler.RestartableTask restartableTask) {
        this.restartableTask = requireNonNull(restartableTask);
        this.completer = new SequentialScheduler.TryEndDeferredCompleter();
        this.schedulableTask = new SequentialScheduler.SchedulableTask();
    }

    /**
     * Runs or schedules the task to be run.
     *
     * @implSpec The recursion which is possible here must be bounded:
     *
     *               <pre>
     * {@code
     *     this.runOrSchedule()
     *         completer.complete()
     *             this.runOrSchedule()
     *                 ...
     * }
     * </pre>
     *
     * @implNote The recursion in this implementation has the maximum depth of 1.
     */
    public void runOrSchedule() {
        runOrSchedule(schedulableTask, null);
    }

    /**
     * Executes or schedules the task to be executed in the provided executor.
     * <p>
     * This method can be used when potential executing from a calling thread is not desirable.
     *
     * @param executor An executor in which to execute the task, if the task needs to be executed.
     * @apiNote The given executor can be {@code null} in which case calling {@code runOrSchedule(null)}
     *              is strictly equivalent to calling {@code runOrSchedule()}.
     */
    public void runOrSchedule(Executor executor) {
        runOrSchedule(schedulableTask, executor);
    }

    private void runOrSchedule(SequentialScheduler.SchedulableTask task, Executor executor) {
        while (true) {
            int s = state.get();
            if (s == END) {
                if (state.compareAndSet(END, BEGIN)) {
                    break;
                }
            } else if ((s & BEGIN) != 0) {
                // Tries to change the state to AGAIN, preserving OFFLOAD bit
                if (state.compareAndSet(s, AGAIN | (s & OFFLOAD))) {
                    return;
                }
            } else if ((s & AGAIN) != 0 || s == STOP) {
                /*
                 * In the case of AGAIN the scheduler does not provide happens-before relationship between actions
                 * prior to runOrSchedule() and actions that happen in task.run(). The reason is that no volatile
                 * write is done in this case, and the call piggybacks on the call that has actually set AGAIN
                 * state.
                 */
                return;
            } else {
                // Non-existent state, or the one that cannot be offloaded
                throw new InternalError(String.valueOf(s));
            }
        }
        if (executor == null) {
            task.run();
        } else {
            executor.execute(task);
        }
    }

    /** The only concrete {@code DeferredCompleter} implementation. */
    private class TryEndDeferredCompleter extends SequentialScheduler.DeferredCompleter {

        @Override
        public void complete() {
            while (true) {
                int s;
                while (((s = state.get()) & OFFLOAD) != 0) {
                    // Tries to offload ending of the task to the parent
                    if (state.compareAndSet(s, s & ~OFFLOAD)) {
                        return;
                    }
                }
                while (true) {
                    if ((s & OFFLOAD) != 0) {
                        /*
                         * OFFLOAD bit can never be observed here. Otherwise it would mean there is another invocation of
                         * "complete" that can run the task.
                         */
                        throw new InternalError(String.valueOf(s));
                    }
                    if (s == BEGIN) {
                        if (state.compareAndSet(BEGIN, END)) {
                            return;
                        }
                    } else if (s == AGAIN) {
                        if (state.compareAndSet(AGAIN, BEGIN | OFFLOAD)) {
                            break;
                        }
                    } else if (s == STOP) {
                        return;
                    } else if (s == END) {
                        throw new IllegalStateException("Duplicate completion");
                    } else {
                        // Non-existent state
                        throw new InternalError(String.valueOf(s));
                    }
                    s = state.get();
                }
                restartableTask.run(completer);
            }
        }
    }

    /**
     * Tells whether, or not, this scheduler has been permanently stopped.
     * <p>
     * Should be used from inside the task to poll the status of the scheduler, pretty much the same way
     * as it is done for threads:
     * 
     * <pre>
     * {@code
     *     if (!Thread.currentThread().isInterrupted()) {
     *         ...
     *     }
     * }
     * </pre>
     */
    public boolean isStopped() {
        return state.get() == STOP;
    }

    /**
     * Stops this scheduler. Subsequent invocations of {@code runOrSchedule} are effectively no-ops.
     * <p>
     * If the task has already begun, this invocation will not affect it, unless the task itself uses
     * {@code isStopped()} method to check the state of the handler.
     */
    public void stop() {
        state.set(STOP);
    }

    /**
     * Returns a new {@code SequentialScheduler} that executes the provided {@code mainLoop} from within
     * a {@link SequentialScheduler.SynchronizedRestartableTask}.
     *
     * @apiNote This is equivalent to calling
     *              {@code new SequentialScheduler(new SynchronizedRestartableTask(mainLoop))} The main
     *              loop
     *              must not perform any blocking operation.
     * @param mainLoop The main loop of the new sequential scheduler
     * @return a new {@code SequentialScheduler} that executes the provided {@code mainLoop} from within
     *             a {@link SequentialScheduler.SynchronizedRestartableTask}.
     */
    public static SequentialScheduler synchronizedScheduler(Runnable mainLoop) {
        return new SequentialScheduler(new SequentialScheduler.SynchronizedRestartableTask(mainLoop));
    }
}
