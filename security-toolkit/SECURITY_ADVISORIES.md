Security Advisories
========================

This file lists known security vulnerabilities in the NanoSat MO Framework and their status, both fixed and open. Each entry follows the structure of a GitHub security advisory, so that it can be published as one.

Only vulnerabilities that are already public, or already fixed, are listed here. To report a new, undisclosed vulnerability, see [SECURITY.md](../SECURITY.md); it must be reported privately, not added to this file.

### Index

| ID | Title | Status | Affected | Fixed in | GHSA / CVE |
|---|---|---|---|---|---|
| [NMF-2026-001](#nmf-2026-001-unsafe-deserialization-in-the-spacecraft-simulator) | Unsafe deserialization in the spacecraft simulator | Fixed | <= 4.0 | [5.0](https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0) | CVE pending |
| [NMF-2026-002](#nmf-2026-002-unsafe-deserialization-of-parameter-and-action-values) | Unsafe deserialization of parameter and action values | Fixed | up to 6.0 | 6.0 | CVE pending |

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

---

### NMF-2026-002: Unsafe deserialization of parameter and action values

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published. CVE requested, ID not yet assigned. |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-composites` (the deserialization helper is `HelperAttributes` in the MO library `int.esa.ccsds.mo:api-area001-v003-mal`) |
| Affected versions | All versions up to 6.0, before the fix |
| Fixed in | 6.0 |
| Reported | Thales, Hack CYSAT 2023 |
| Severity | High (unauthenticated, remote; full remote code execution where a gadget chain is on the classpath) |
| Weaknesses | CWE-502: Deserialization of Untrusted Data |

#### Impact

An application built on the NMF Monitor and Control adapters deserializes attacker-controlled bytes with `java.io.ObjectInputStream.readObject()` and no allow-list or `ObjectInputFilter`.

When a parameter or action value arrives as a MAL `Blob`, the adapter passes its raw bytes straight to `HelperAttributes.blobAttribute2serialObject()`, which calls `readObject()` on them. The bytes are controlled by the remote MAL peer, so the peer chooses which classes are instantiated and which deserialization callbacks (`readObject`, `readResolve`) run in the victim process. MO access control is optional and off by default, so no credentials are required unless a deployment adds them.

The sinks are:

* Provider side: `ParameterProviderServiceImpl.setValue` → `ParameterManager` → `SimpleMonitorAndControlAdapter.onSetValue` (and `actionArrived`) → `HelperAttributes.blobAttribute2serialObject`. A remote consumer that sets a parameter raw value of type `Blob` reaches it.
* Consumer side: `MOAdapterImpl.monitorValueNotifyReceived`. A rogue or compromised provider pushing a parameter notification reaches it on the operator's host. One example, `camera-acquisitor-system-ground`, has Spring on its classpath, which is known to carry deserialization gadget chains.

The minimum impact, with no gadget chain, is the forced instantiation of arbitrary classpath classes and the execution of their deserialization callbacks. The maximum impact, with a gadget chain, is remote code execution and full compromise of the affected process. Thales demonstrated this at Hack CYSAT 2023 as a covert channel: an app with no overtly malicious code in its submitted binary received a crafted `Blob` parameter and ran arbitrary code on board.

#### Workarounds

* Do not define `Blob` parameters or actions whose values are deserialized, and do not rely on the framework's automatic object serialization.
* Enable MO access control so that only authorised peers can set values or push notifications.
* Keep providers and consumers on a trusted network, and keep known gadget-bearing libraries off the classpath.

#### Fix

The automatic serialization of objects into, and deserialization of objects from, a `Blob` was removed from the Monitor and Control adapters (`SimpleMonitorAndControlAdapter`, `NMFProvider`), from the consumer adapter (`MOAdapterImpl`), and from the Consumer Test Tool. Parameter and action values are now restricted to the MO attribute types: a value that is not an MO attribute type is rejected with a logged error instead of being serialized, and an incoming `Blob` is passed through as bytes and never deserialized. No untrusted bytes are passed to `ObjectInputStream.readObject()`. The `serialized-object` example, which relied on the feature, was removed.

The unsafe helper `HelperAttributes.blobAttribute2serialObject` still exists in the MO library, but the framework no longer calls it.

#### References

* Thales, "First worldwide hacking demo of an in-orbit satellite", Hack CYSAT 2023: https://www.youtube.com/watch?v=sXGQWLJ8904
* Sink (before the fix): `SimpleMonitorAndControlAdapter.onSetValue` / `actionArrived`, and `MOAdapterImpl.monitorValueNotifyReceived`, calling `HelperAttributes.blobAttribute2serialObject`.
* Covered by the regression test `Nmf2026002Test` in this toolkit, which guards against the deserialization being reintroduced.
