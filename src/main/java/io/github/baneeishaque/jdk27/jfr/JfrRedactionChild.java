package io.github.baneeishaque.jdk27.jfr;

/**
 * Child VM for the JEP 536 demo: started with a secret on its command line
 * ({@code -Ddb.password=...}), it only needs to live long enough for the
 * startup recording to capture the initial system properties.
 */
public final class JfrRedactionChild {

    private JfrRedactionChild() {
    }

    /**
     * Keeps the VM alive briefly so the startup recording is written.
     *
     * @param args ignored
     * @throws InterruptedException when interrupted while sleeping
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("child up: " + JfrRedactionChild.class.getName());
        Thread.sleep(300);
    }
}
