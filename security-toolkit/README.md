ESA NMF Security Toolkit
========================

Security projects for the NanoSat MO Framework.

### Layout

* `vulnerability-tests/` — the tests for the vulnerabilities in
  [SECURITY_ADVISORIES.md](SECURITY_ADVISORIES.md). Each test drives the vulnerable component and
  asserts that the fix is in place; a test for a vulnerability that is still open fails until it is
  fixed.
* `app-privilege-probe/` — a benign NMF app that reports the privileges the Supervisor gave it. A
  test installs and runs it through the real Supervisor to check that app isolation confines it.
* `app-directory-probe/` — a benign NMF app that tries to take over the Supervisor's entry in the
  Directory service, packaged both as `directory-probe` and as `nanosat-mo-supervisor`. A test
  installs and runs each package through the real Supervisor and checks that ground still reaches it.
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

The container tests need some artifacts built first; each one skips (rather than fails) if its
prerequisites are missing. From the repository root:

```
mvn -DskipTests install
mvn -DskipTests -Passembly-with-dependencies -pl nmf-mission-simulator-orekit/cubesat-spacecraft-simulator,sdk/cli-tool install
mvn -DskipTests -f security-toolkit/pom.xml -pl app-privilege-probe package
mvn -DskipTests -pl nmf-mission-barebone/barebone-space-filesystem package -Pgenerate-filesystem -Dgenerate-filesystem.appsIsolation=linux-userspace
mvn test -f security-toolkit/pom.xml
```

The exact steps CI runs are in [`.github/workflows/security.yml`](../.github/workflows/security.yml).
