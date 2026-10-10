Security Advisories
========================

This file lists the security vulnerabilities that were found and fixed in the NanoSat MO Framework. Each entry follows the structure of a GitHub security advisory, so that it can be published as one.

Vulnerabilities that are not yet fixed are not listed here. To report a vulnerability, see [SECURITY.md](../SECURITY.md).

### Index

| ID | Title | Affected | Patched | Fixed | GHSA / CVE |
|---|---|---|---|---|---|
| [NMF-2026-001](#nmf-2026-001-unsafe-deserialization-in-the-spacecraft-simulator) | Unsafe deserialization in the spacecraft simulator | <= 4.0 | 5.0 | 2026-07-10 | CVE pending |

---

### NMF-2026-001: Unsafe deserialization in the spacecraft simulator

| Field | Value |
|---|---|
| GHSA / CVE | GHSA not published. CVE requested from MITRE by the reporter, ID not yet assigned. |
| Ecosystem | Maven |
| Package | `int.esa.nmf.simulator:spacecraft-simulator` |
| Affected versions | <= 4.0 |
| Patched versions | 5.0 (artifact renamed to `int.esa.nmf.simulator:cubesat-spacecraft-simulator`) |
| Reported | 2026-07-07 |
| Fixed | 2026-07-10 (commits 4e2a55b and 19f1536), released in 5.0 on 2026-07-21 |
| Severity | To be assessed |
| Weaknesses | CWE-502: Deserialization of Untrusted Data; CWE-1327: Binding to an Unrestricted IP Address |
| Credits | To be confirmed |

#### Impact

The spacecraft simulator exchanges serialized Java objects over TCP between its server (port 11111 by default) and its clients, such as the simulator GUI. Up to version 4.0, both sides deserialized the received objects without an `ObjectInputFilter`, and the server listened on all network interfaces, regardless of the configured listen address. Configuring a loopback address therefore did not restrict exposure.

An unauthenticated attacker who can reach the server port can send crafted serialized objects, which the server deserializes without restriction. This allows denial of service through object graphs that exhaust memory or CPU, regardless of the classpath, and remote code execution if a known deserialization gadget chain is on the simulator's classpath. A malicious server can do the same to a client that connects to it.

The simulator is a development and test tool. It does not run on board a spacecraft.

#### Patches

Fixed in version 5.0, by commits 4e2a55b and 19f1536:

* The server accepts only the command types that clients send, and the JDK scalar and collection types they contain. The client accepts only the simulator's own classes and JDK types (`SimulatorSerialFilter`).
* Both sides limit the depth of the received object graphs and the length of any single array.
* The server binds to the configured listen address instead of all network interfaces.

Version 5.0 also limited the number of references and the number of bytes read. Version 5.1 removed these two limits (commit e19b450): the JDK counts them over the whole connection, so they rejected legitimate traffic on long-lived connections.

The reporter verified the fix with the original proof of concept.

#### Workarounds

Do not expose the simulator's TCP port to an untrusted network. Restrict access to the port with a firewall, or run the simulator on an isolated host.

#### References

* Fix: commits [4e2a55b](https://github.com/esa/nanosat-mo-framework/commit/4e2a55b24c0a4ff59a1c2c972d6231986c3a68b4) and [19f1536](https://github.com/esa/nanosat-mo-framework/commit/19f15364f9809f6c29e70877c18d6c65f521f00e)
* Change to the limits in 5.1: commit [e19b450](https://github.com/esa/nanosat-mo-framework/commit/e19b4501f2f855a115791112422c4d62a67925aa)
* Release 5.0: https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0
