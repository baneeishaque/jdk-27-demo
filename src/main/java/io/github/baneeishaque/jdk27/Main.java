package io.github.baneeishaque.jdk27;

import io.github.baneeishaque.jdk27.concurrency.StructuredConcurrency;
import io.github.baneeishaque.jdk27.gc.G1Default;
import io.github.baneeishaque.jdk27.headers.CompactObjectHeaders;
import io.github.baneeishaque.jdk27.jfr.JfrRedaction;
import io.github.baneeishaque.jdk27.lazy.LazyConstants;
import io.github.baneeishaque.jdk27.orders.OrderPipeline;
import io.github.baneeishaque.jdk27.patterns.PrimitivePatterns;
import io.github.baneeishaque.jdk27.pem.PemEncoding;
import io.github.baneeishaque.jdk27.tls.PostQuantumTls;
import io.github.baneeishaque.jdk27.vector.VectorApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Command-line entry point for the JDK 27 showcase.
 *
 * <p>With no arguments every demo runs in a fixed order; with one argument
 * only the matching demo runs. Each demo prints a short human-readable report
 * so the whole feature set can be exercised from a single command.
 */
public final class Main {

    private Main() {
    }

    private static final Map<String, Supplier<String>> DEMOS = new LinkedHashMap<>();

    static {
        DEMOS.put("gc", G1Default::report);
        DEMOS.put("headers", CompactObjectHeaders::report);
        DEMOS.put("jfr", JfrRedaction::report);
        DEMOS.put("lazy", LazyConstants::report);
        DEMOS.put("patterns", PrimitivePatterns::report);
        DEMOS.put("concurrency", StructuredConcurrency::report);
        DEMOS.put("orders", OrderPipeline::report);
        DEMOS.put("tls", PostQuantumTls::report);
        DEMOS.put("vector", VectorApi::report);
        DEMOS.put("pem", PemEncoding::report);
    }

    /**
     * Runs the requested demo (or all demos) and prints the results.
     *
     * @param args optional single demo id; when omitted, all demos run
     */
    public static void main(String[] args) {
        System.out.printf("JDK %s — nine JEPs, one showcase%n%n", Runtime.version());
        if (args.length > 1) {
            System.err.println("usage: java io.github.baneeishaque.jdk27.Main [demo-id]");
            System.err.println("demo ids: " + String.join(", ", DEMOS.keySet()));
            System.exit(2);
        }
        if (args.length == 1) {
            Supplier<String> demo = DEMOS.get(args[0]);
            if (demo == null) {
                System.err.println("unknown demo '" + args[0] + "'; ids: " + String.join(", ", DEMOS.keySet()));
                System.exit(2);
            }
            System.out.println(demo.get());
            return;
        }
        DEMOS.forEach((id, demo) -> {
            System.out.println("== " + id + " ==");
            try {
                System.out.println(demo.get());
            } catch (RuntimeException failure) {
                System.out.println("FAILED: " + failure);
            }
            System.out.println();
        });
    }
}
