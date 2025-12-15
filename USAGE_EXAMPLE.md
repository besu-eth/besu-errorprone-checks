# RedundantPicoCliArity Usage Example

## Problem
The `RedundantPicoCliArity` check detects unnecessary `arity = "1"` specifications in PicoCLI `@Option` annotations, since single-value options default to `arity = "1"`.

## Solution: Use @SuppressWarnings for intentional cases

For existing Boolean fields where `arity = "1"` is intentional (like in P2PDiscoveryOptions), suppress the warning:

```java
// Before: This will trigger RedundantPicoCliArity warning
@CommandLine.Option(
    names = {"--p2p-enabled"},
    description = "Enable P2P functionality (default: ${DEFAULT-VALUE})",
    arity = "1")
public final Boolean p2pEnabled = true;

// After: Suppress the warning for intentional Boolean usage
@SuppressWarnings("RedundantPicoCliArity")
@CommandLine.Option(
    names = {"--p2p-enabled"},
    description = "Enable P2P functionality (default: ${DEFAULT-VALUE})",
    arity = "1")
public final Boolean p2pEnabled = true;
```

## Examples of what gets flagged

**Will be flagged (redundant):**
```java
@Option(names = "--file", arity = "1")  // Redundant for String
private String fileName;

@Option(names = "--count", arity = "1") // Redundant for Integer
private Integer count;
```

**Will NOT be flagged (valid):**
```java
@Option(names = "--files", arity = "0..*")  // Multiple values
private List<String> files;

@Option(names = "--range", arity = "2")     // Exactly 2 values
private String[] range;

@Option(names = "--flag", arity = "0")      // Boolean flag
private boolean flag;

@SuppressWarnings("RedundantPicoCliArity")  // Intentionally suppressed
@Option(names = "--enabled", arity = "1")
private Boolean enabled;
```

This approach allows the cleanup work to proceed while preserving intentional Boolean field usage patterns.