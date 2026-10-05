package io.github.baneeishaque.jdk27.concurrency;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * JEP 533 — Structured Concurrency (seventh preview).
 *
 * <p>Every fork happens inside a scope; when the scope closes, no subtask is
 * still running. This class demonstrates the three canonical shapes: fan-out
 * with the default all-successful joiner, racing with
 * {@code anySuccessfulOrThrow}, and a scope-wide timeout configured through
 * {@code Configuration#withTimeout}.
 */
public final class StructuredConcurrency {

    private StructuredConcurrency() {
    }

    /**
     * Simulates a remote call that sleeps for the given time.
     *
     * @param name   result label
     * @param millis simulated latency in milliseconds
     * @return the label with its latency
     * @throws InterruptedException when interrupted while sleeping
     */
    public static String fetch(String name, long millis) throws InterruptedException {
        Thread.sleep(millis);
        return name + " (" + millis + "ms)";
    }

    /**
     * Fans two calls out concurrently and joins both.
     *
     * @return both results
     * @throws ExecutionException   when any subtask fails
     * @throws InterruptedException when interrupted while joining
     */
    public static String fanOut() throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            Subtask<String> profile = scope.fork(() -> fetch("profile", 80));
            Subtask<String> orders = scope.fork(() -> fetch("orders", 120));
            scope.join();
            return profile.get() + ", " + orders.get();
        }
    }

    /**
     * Races three calls and keeps the first successful result.
     *
     * @return the first successful result
     * @throws ExecutionException   when every subtask fails
     * @throws InterruptedException when interrupted while joining
     */
    public static String firstSuccess() throws ExecutionException, InterruptedException {
        try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow())) {
            scope.fork(() -> fetch("slow", 300));
            scope.fork(() -> {
                Thread.sleep(50);
                throw new IllegalStateException("fast failure is ignored");
            });
            scope.fork(() -> fetch("fast", 150));
            return scope.join();
        }
    }

    /**
     * Cancels a scope when a fixed timeout elapses.
     *
     * @return a description of the timeout outcome
     */
    public static String timeout() {
        try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulOrThrow(),
                configuration -> configuration.withTimeout(Duration.ofMillis(100)))) {
            scope.fork(() -> fetch("too-slow", 2_000));
            return "unexpected success: " + scope.join();
        } catch (ExecutionException failure) {
            if (failure.getCause() instanceof StructuredTaskScope.CancelledByTimeoutException) {
                return "scope cancelled after 100ms";
            }
            return "unexpected failure: " + failure;
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return "interrupted";
        }
    }

    /**
     * Shows failure propagation: one failed subtask cancels its siblings.
     *
     * @return a description of the propagated failure
     */
    public static String failurePropagation() {
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> fetch("healthy", 50));
            scope.fork(() -> {
                throw new IllegalStateException("payment declined");
            });
            scope.join();
            return "unreachable";
        } catch (ExecutionException failure) {
            return "siblings cancelled; cause: " + failure.getCause().getMessage();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return "interrupted";
        }
    }

    /**
     * Produces the demo report.
     *
     * @return human-readable summary of all three scope shapes
     */
    public static String report() {
        try {
            long start = System.nanoTime();
            String results = fanOut();
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            return "fan-out: " + results + " in " + elapsedMillis + "ms (not 200ms)" + System.lineSeparator()
                    + "first success: " + firstSuccess() + System.lineSeparator()
                    + "timeout: " + timeout() + System.lineSeparator()
                    + "failure: " + failurePropagation();
        } catch (ExecutionException | InterruptedException failure) {
            if (failure instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return "demo failed: " + failure;
        }
    }
}
