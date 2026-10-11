ESA NMF Security Toolkit
========================

Security projects for the NanoSat MO Framework.

### Layout

* `vulnerability-tests/` — the tests for the vulnerabilities in
  [SECURITY_ADVISORIES.md](SECURITY_ADVISORIES.md). Each test drives the vulnerable component and
  asserts that the fix is in place; a test for a vulnerability that is still open fails until it is
  fixed.
* Further projects will be added, such as a malicious app used to exercise the framework's
  defences.

### Vulnerability tests

Each test is named after the advisory it covers (for example `Nmf2026001Test`). A test exercises the
component either in a container (for a network-facing component, so a still-vulnerable version can be
run without putting the host at risk) or in-process (for a self-contained check whose payload is
fully under the test's control). The probes are benign: they detect a weakness without exploiting
it. See [SECURITY_ADVISORIES.md](SECURITY_ADVISORIES.md) for the vulnerabilities themselves.

### Requirements

* JDK 21
* A running Docker daemon for the container tests ([Testcontainers](https://java.testcontainers.org/))

### Running

The tests need the framework installed and the simulator fat jar, which is built by the
`assembly-with-dependencies` profile. From the repository root:

```
mvn -DskipTests -Passembly-with-dependencies -pl core/nmf-composites,nmf-mission-simulator-orekit/cubesat-spacecraft-simulator -am install
mvn test -f security-toolkit/pom.xml
```
