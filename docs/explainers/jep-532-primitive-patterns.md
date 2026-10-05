# Explainer: JEP 532 — primitive types in patterns

## What changed

Primitive type patterns extend `instanceof`, `switch`, and record deconstruction to
primitive types. A pattern like `JsonValue.JsonNumber(int i)` matches a `double` component
only when the value converts to `int` **exactly**; otherwise the match falls through to the
next case. This turns hand-written range checks into pattern selection.

## The demo

[`PrimitivePatterns`](../../src/main/java/io/github/baneeishaque/jdk27/patterns/PrimitivePatterns.java)
models a minimal JSON number hierarchy — JSON has one number type, so the record stores a
`double` — and classifies values by which primitive pattern matches first:

```java
public sealed interface JsonValue {                       // :17
    record JsonNumber(double value) implements JsonValue { }
    record JsonString(String value) implements JsonValue { }
    record JsonBool(boolean value) implements JsonValue { }
}

public static String describe(JsonValue value) {          // :38
    return switch (value) {
        case JsonValue.JsonNumber(int i) -> "int-exact number: " + i;
        case JsonValue.JsonNumber(long l) -> "long number: " + l;
        case JsonValue.JsonNumber(double d) -> "double-rounded number: " + d;
        case JsonValue.JsonString(String s) -> "string of length " + s.length();
        case JsonValue.JsonBool(boolean b) -> "boolean: " + b;
    };
}
```

Ordering carries the semantics: `int` first (narrowest exact match), then `long`, then the
`double` fallback. The compiler enforces that the switch stays exhaustive over the sealed
hierarchy.

Note the qualification: nested record types are referenced as `JsonValue.JsonNumber` in
case labels, because the simple name of a type nested two levels deep is not in scope.

## Exactness is the whole point

```text
int-exact number: 42
long number: 5000000000
double-rounded number: 0.1
42 fits in int: true
5_000_000_000 fits in int: false
```

- `42.0` converts to `int` exactly → first case.
- `5_000_000_000.0` does not fit an `int` but does fit a `long` exactly → second case.
- `0.1` fits neither → the `double` case is reached.

`fitsInInt` (`:54`) shows the same machinery in `instanceof` form:

```java
return value instanceof JsonValue.JsonNumber(int i) && i == i;
```

If the pattern matches, the value *was* exactly representable — the predicate needs no
range math.

## Notes for practitioners

- Pattern order is your precedence declaration; put narrow patterns first.
- The exactness rule means no silent truncation: `case int i` on a `double` selector never
  rounds `0.9` to `0`.
- This is still a preview: compile and run with `--enable-preview --release 27`.
- For a `long` component, a trailing `double` case is effectively unreachable — every
  `long` is long-exact. Match on a `double`-typed component to get a meaningful fallback.
