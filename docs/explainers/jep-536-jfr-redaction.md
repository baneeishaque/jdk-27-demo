# Explainer: JEP 536 — JFR in-process redaction

## What changed

Flight Recorder events can carry sensitive data: initial system properties and environment
variables are recorded at startup, and command-line arguments are visible to event
producers. Historically you either trusted the file or scrubbed it after the fact. JDK 27
redacts in-process, **before** the recording reaches disk, a temp directory, or a remote
stream: matching values become the literal string `[REDACTED]`. Filter lists are
configurable via `-XX:FlightRecorderOptions`.

## The demo

Redaction is installed at VM startup, so the demo launches a child VM with a recording
already running and a secret planted on its command line:

```java
Process process = new ProcessBuilder(                     // :39
        java, "--enable-preview",
        "-XX:StartFlightRecording:filename=" + recording,
        "-Ddb.password=" + SECRET,
        "-cp", System.getProperty("java.class.path"),
        JfrRedactionChild.class.getName()) ...
```

The parent then reads the recording with the unchanged `jdk.jfr.consumer` API:

```java
public static List<String> recordedSystemProperty(Path recording, String property) throws IOException {   // :72
    return RecordingFile.readAllEvents(recording).stream()
            .filter(event -> event.getEventType().getName().equals("jdk.InitialSystemProperty"))
            .filter(event -> property.equals(event.getString("key")))
            .map(event -> event.getString("value"))
            .toList();
}
```

Note the JDK 27 API shape: `readAllEvents` is now a **static** method taking a `Path`; the
`open(...)` + instance `readAllEvents()` pair is gone. A companion check
(`containsRawSecret`, `:104`) scans every event's `toString()` for the secret, so the demo
proves redaction rather than spot-checking one event type.

## What we measured

```text
recording: target/jfr-demo/redaction-demo.jfr
jdk.InitialSystemProperty[db.password] = [[REDACTED]]
raw secret present anywhere: false
```

## Notes for practitioners

- Redaction applies to the *values* of command-line arguments, environment variables, and
  system properties; keys remain, which keeps recordings useful for diagnosis.
- If a log-scraping pipeline depended on reading an env var from a recording, configure
  `-XX:FlightRecorderOptions` filter lists rather than disabling redaction wholesale.
- The guarantee is about the recording artifact — printing the same secret from application
  code still leaks it the old-fashioned way.
- Use JFR's `-XX:StartFlightRecording` in a sidecar/child process when you need
  startup-time events; by the time an application is up, the interesting events happened.
