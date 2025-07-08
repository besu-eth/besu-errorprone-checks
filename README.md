# Besu custom error-prone checks
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/hyperledger/besu-errorprone-checks/blob/main/LICENSE)
[![Discord](https://img.shields.io/discord/905194001349627914?logo=Hyperledger&style=plastic)](https://discord.gg/hyperledger)
[![Twitter Follow](https://img.shields.io/twitter/follow/HyperledgerBesu)](https://twitter.com/HyperledgerBesu)

This repository contains custom error-prone checks for [Hyperledger Besu](https://github.com/hyperledger/besu/).

## Usage
These [custom error-prone checks](https://errorprone.info/docs/plugins) are used in the Besu codebase. To use these checks in your project:

1. Add the following repository definition to your gradle file:
```groovy
  repositories {
    maven {
        url 'https://hyperledger.jfrog.io/hyperledger/besu-maven'
        content { includeGroupByRegex('org\\.hyperledger\\..*') }
    }
  }
```
2. Add the following dependency to your gradle file:
```groovy
dependencies {
    errorprone("org.hyperledger.errorpronechecks:besu-errorprone-checks:1.1.0")
}
```

## List of Checks
The following custom error-prone checks are included in this repository:
- Banned Methods
- Avoid creating SecureRandom instances directly
- Avoid creating MessageDigest instances directly
- Do not return null Optionals
- Experimental CLI options annotation must be set `hidden` to `true`
- Method input parameters must be final
- Prefer java not-null annotations over JetBrains not-null annotations
- Loggers modifiers must be private, static, and final

The creation of custom errorprone checkers was largely derived from:
* https://github.com/tbroyer/gradle-errorprone-plugin
* https://errorprone.info/docs/installation
* https://github.com/google/error-prone/wiki/Writing-a-check

To allow for debugging from within intellij, the following must be added to the VM args
in the run/debug configuration (this assumes your gradle cache is at the default location under
your home):
`-Xbootclasspath/p:${HOME}/.gradle/caches/./modules-2/files-2.1/com.google.errorprone/javac/9+181-r4173-1/bdf4c0aa7d540ee1f7bf14d47447aea4bbf450c5/javac-9+181-r4173-1.jar`
