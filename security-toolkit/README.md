ESA NMF Security Toolkit
========================

Security regression tests for the NanoSat MO Framework.

Each test drives a component that had a known vulnerability and asserts that the fix is still in
place, so that the vulnerability cannot return unnoticed. The vulnerabilities are listed in
[ADVISORIES.md](ADVISORIES.md).

The component under test runs inside a container, driven by a probe
(`esa.mo.nmf.security.probe.SecurityProbe`). The container confines the component, so a version
that is still vulnerable can be exercised without putting the host at risk. The probes are benign:
they detect a weakness without exploiting it.

### Requirements

* JDK 21
* A running Docker daemon (the tests use [Testcontainers](https://java.testcontainers.org/))

### Running

The tests need the simulator fat jar, which is built by the `assembly-with-dependencies` profile.
From the repository root:

```
mvn -DskipTests -Passembly-with-dependencies install
mvn test -f security-toolkit/pom.xml
```

### Current coverage

* **NMF-2026-001** — the spacecraft simulator's TCP transport binds to the configured loopback
  address only, and rejects a class that is not on its deserialization allow-list.
