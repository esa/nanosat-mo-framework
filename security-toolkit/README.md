ESA NMF Security Toolkit
========================

Security projects for the NanoSat MO Framework.

### Layout

* `vulnerability-tests/` — one test per advisory. Each test drives the vulnerable component and
  asserts that the fix is in place. A test for a vulnerability that is still open fails until it is
  fixed. The vulnerabilities are listed in [ADVISORIES.md](ADVISORIES.md).
* Further projects will be added, such as a malicious app used to exercise the framework's
  defences.

### Vulnerability tests

A test exercises the component either in a container (for a network-facing component, so a
still-vulnerable version can be run without putting the host at risk) or in-process (for a
self-contained check whose payload is fully under the test's control). The probes are benign: they
detect a weakness without exploiting it.

Coverage:

* **NMF-2026-001** (fixed) — the spacecraft simulator's TCP transport binds to the configured
  loopback address only and rejects a class that is not on its deserialization allow-list. Run in a
  container.
* **NMF-2026-002** (fixed) — the Monitor and Control adapters must not deserialize an untrusted Blob
  parameter or action value. Run in-process.

### Requirements

* JDK 21
* A running Docker daemon for the container tests ([Testcontainers](https://java.testcontainers.org/))

### Running

The container test needs the simulator fat jar, built by the `assembly-with-dependencies` profile.
From the repository root:

```
mvn -DskipTests -Passembly-with-dependencies -pl nmf-mission-simulator-orekit/cubesat-spacecraft-simulator -am install
mvn test -f security-toolkit/pom.xml
```
