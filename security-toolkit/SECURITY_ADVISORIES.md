Security Advisories
========================

This file lists known security vulnerabilities in the NanoSat MO Framework and their status, both fixed and open. Each entry follows the structure of a GitHub security advisory, so that it can be published as one.

Only vulnerabilities that are already public, or already fixed, are listed here. To report a new, undisclosed vulnerability, see [SECURITY.md](../SECURITY.md); it must be reported privately, not added to this file.

### Index

| ID | Title | Status | Affected | Fixed in | GHSA / CVE |
|---|---|---|---|---|---|
| [NMF-2026-001](#nmf-2026-001-unsafe-deserialization-in-the-spacecraft-simulator) | Unsafe deserialization in the spacecraft simulator | Fixed | <= 4.0 | [5.0](https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0) | CVE pending |
| [NMF-2026-002](#nmf-2026-002-unsafe-deserialization-of-parameter-and-action-values) | Unsafe deserialization of parameter and action values | Fixed | up to 6.0 | 6.0 | CVE pending |
| [NMF-2026-003](#nmf-2026-003-supervisor-and-apps-run-with-excessive-privileges) | Supervisor and apps run with excessive privileges | Fixed | up to 3.0.0 | 3.0.0, [5.0](https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0) | — |
| [NMF-2026-004](#nmf-2026-004-os-command-injection-through-the-app-name-in-the-linux-launcher) | OS command injection through the app name in the Linux launcher | Fixed | >= 2.1.0, < 6.0 | 6.0 | — |

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

[1] Fix: commits [4e2a55b](https://github.com/esa/nanosat-mo-framework/commit/4e2a55b24c0a4ff59a1c2c972d6231986c3a68b4) and [19f1536](https://github.com/esa/nanosat-mo-framework/commit/19f15364f9809f6c29e70877c18d6c65f521f00e)

[2] Change to the limits in 5.1: commit [e19b450](https://github.com/esa/nanosat-mo-framework/commit/e19b4501f2f855a115791112422c4d62a67925aa)

[3] Release 5.0: https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0

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

[1] Thales, "First worldwide hacking demo of an in-orbit satellite", Hack CYSAT 2023: https://www.youtube.com/watch?v=sXGQWLJ8904

[2] Sink (before the fix): `SimpleMonitorAndControlAdapter.onSetValue` / `actionArrived`, and `MOAdapterImpl.monitorValueNotifyReceived`, calling `HelperAttributes.blobAttribute2serialObject`.

[3] Covered by the regression test `Nmf2026002Test` in this toolkit, which guards against the deserialization being reintroduced.

---

### NMF-2026-003: Supervisor and apps run with excessive privileges

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | The supervisor and App launcher (`int.esa.nmf.core:nmf-services-impl`), and the deployment generated by `int.esa.nmf.core:nmf-linux-maven-plugin` |
| Affected versions | The mainline before the 3.0.0 consolidation, as deployed on OPS-SAT (supervisor as root, apps not isolated). Phi-Sat-2, on its own branch, was not affected. |
| Fixed in | 3.0.0 (the privilege separation consolidated into the mainline); generalized into isolation modes in 5.0 |
| Reported | Thales, Hack CYSAT 2023 |
| Severity | High (privilege escalation to full control of the platform) |
| Weaknesses | CWE-250: Execution with Unnecessary Privileges; CWE-269: Improper Privilege Management |

#### Impact

On OPS-SAT, the NMF supervisor ran as root, and the apps it managed ran without isolation, under the same privileges. The supervisor is the gatekeeper between the apps and the platform (the sensors and actuators), so any code that ran in the supervisor, or in any app, had root over the whole on-board computer.

This turns a foothold into full control. Combined with the unsafe deserialization of parameter values ([NMF-2026-002](#nmf-2026-002-unsafe-deserialization-of-parameter-and-action-values)), an unprivileged, innocuous-looking app could reach remote code execution in the supervisor and so run as root. Thales demonstrated this chain at Hack CYSAT 2023. They escalated further through an OPS-SAT-specific root daemon on the CAN bus, which is not part of NMF.

Phi-Sat-2 was not affected. It already ran with the privilege separation that was later consolidated at version 3.0.0 — the supervisor as the dedicated unprivileged `nmf-admin` user, and each app as its own unprivileged Linux user — so compromising an app, or the supervisor, did not grant root over the platform.

#### Fix

The framework no longer requires root, and gives the supervisor and each app their own unprivileged user account:

* Phi-Sat-2 mission: the privilege separation was first developed here. The supervisor runs as a dedicated unprivileged user, `nmf-admin`, instead of root, and each app runs as its own dedicated unprivileged Linux user (`app-<name>` in the `nmf-apps` group). The supervisor creates the app's user when the app is installed and starts the app through `sudo`. It was implemented directly in the framework code and applied unconditionally on Linux. Phi-Sat-2 ran from its own branch, tagged (`phi-sat-2-cdr`, `phi-sat-2-readiness-review`) but never released as a proper version.

* Version 3.0.0: this privilege separation was consolidated into a common baseline, so that the OPS-SAT and Phi-Sat-2 missions, and all later development, build on it.

* Version 5.0: the per-app isolation was generalized into selectable modes:
  * `linux-userspace`: the original approach — each app as its own unprivileged user, the supervisor as `nmf-admin`.
  * `bubblewrap`: each app inside a bubblewrap sandbox.
  * `docker-containers`: each app inside a Docker container.
  * `none`: no isolation.

  The mode is chosen at mission-development time: the mission sets the `nmf-linux-maven-plugin`'s `appsIsolation` parameter, and the plugin bakes it into the generated bootloader configuration and the supervisor's start script, which applies it at startup (as the `esa.mo.nmf.packagemanager.appsIsolation` property). It is a property of the built mission, not a setting changed during operations. The plugin also generates `setup_linux_userspace.sh`, which creates the `nmf-admin` user and the per-app users and groups, so the setup is produced by the framework rather than hardcoded in the framework code as it had been for Phi-Sat-2.

An app's storage directory is owned by the app and the `nmf-admin` group with `rwxrwx---`, so it is not world-accessible.

#### Mission design considerations

The framework provides the mechanisms; choosing the privilege model is a mission-design decision, made when the mission is built, and it depends on the mission's concept of operations. A mission that runs third-party or otherwise untrusted apps — as OPS-SAT did with external experiments — should run the supervisor as a dedicated unprivileged user and build with an isolation mode (`linux-userspace`, `bubblewrap` or `docker-containers`) rather than the default `none`. A mission that runs only apps the designer controls, with no external access, may not need app isolation.

#### References

[1] Thales, "First worldwide hacking demo of an in-orbit satellite", Hack CYSAT 2023: https://www.youtube.com/watch?v=sXGQWLJ8904

[2] App isolation: `AppsIsolationMode` and the `AppsLauncherManager` variants (Linux, Bubblewrap, Docker) in `nmf-services-impl`; the `appsIsolation` parameter of the `nmf-linux-maven-plugin` (`generate-filesystem`) and the `setup_linux_userspace.sh` it generates.

[3] Phi-Sat-2 milestone tags: [`phi-sat-2-cdr`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-cdr) and [`phi-sat-2-readiness-review`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-readiness-review).

[4] Regression tests: the per-mode launch-command confinement in `AppsLauncherManagerLinuxTest`, `AppsLauncherManagerBubblewrapTest` and `AppsLauncherManagerDockerTest` (`nmf-services-impl`); the generation of the selected isolation mode in `BootloaderGeneratorTest` (`nmf-linux-maven-plugin`); and the behavioural `Nmf2026003Test` in this toolkit, which runs the real barebone Supervisor and has it install (PackageManagement) and run (AppsLauncher) the real `privilege-probe` app from its `.nmfpackage`, then compares the modes: under linux-userspace the Supervisor runs the app as its own unprivileged user and it can read neither the supervisor's nor root's files, while under none the app runs as the Supervisor's own user and shares its files (and the Supervisor refuses to run as root).

---

### NMF-2026-004: OS command injection through the app name in the Linux launcher

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl` (in earlier versions, the Software Management services module) |
| Affected versions | >= 2.1.0, < 6.0 (and the 2.0.0 pre-release) |
| Fixed in | 6.0 |
| Reported | Privately, October 2026 |
| Severity | Low (requires the ability to install and run an app, which already runs code of the installer's choosing with the same privileges) |
| Weaknesses | CWE-78: Improper Neutralization of Special Elements used in an OS Command; CWE-20: Improper Input Validation |

#### Impact

On Linux, the Supervisor starts an app by passing a single command string to a shell: `/bin/sh -c`, or `sudo su - app-<name> -c` under linux-userspace isolation. The string is `cd <app directory>; <environment> ./start_app.sh`, and the app directory ends with the app's name. The name comes from the `info.name` field of the package metadata, and nothing checked it, at install or when the Supervisor read the `apps` folder. Characters such as `;`, `&`, `|` or `$` in a package name were therefore acted on by the shell when the app was started, so a package could run commands of its choosing in place of, or before, its start script.

To exploit this, an attacker must place a package in the Supervisor's `packages` folder, install it and run it. Installing and running an app already executes code of the installer's choosing, so the injected commands gain nothing over what the app's own code can do. They run with the same privileges:

* Without isolation (`none`), apps already run as the Supervisor's user.
* Under linux-userspace isolation, the whole string runs inside `su - app-<name>`, so the injected commands run as the app's own unprivileged user, not as the Supervisor. The name must also be accepted as a Linux user name first, which `adduser` refuses on Debian-based systems.
* The Bubblewrap, Docker and Windows launchers pass the app directory as a separate argument, not through a shell, and are not affected.

The flaw is therefore a robustness and defence-in-depth issue rather than a privilege escalation.

#### Workarounds

* Install only packages from trusted sources. This is already necessary, because installing and running an app executes its code.
* Run apps under an isolation mode, so that each app, and anything it runs, is confined to its own user or sandbox.

#### Fix

A package name must now be one plain file name: letters, digits, `.`, `_`, `+` and `-`, starting with a letter or a digit, at most 128 characters. The check is made when a package file is read, so install, upgrade and uninstall all refuse a package with any other name. The Supervisor also ignores a folder in the `apps` folder whose name is not a valid app name.

The Linux launcher now quotes the app directory and each environment value, so that each one reaches the shell as a single word whatever characters it holds. It also runs the start script only if changing to the app directory succeeded, so a `su -` login never runs a script from the user's home directory instead.

#### References

[1] Command assembly: `AppsLauncherManagerLinux.assembleCommand` in `nmf-services-impl`.

[2] Name check: `Metadata.isValidPackageName`, applied in `Metadata.parseZipFile` and when the Supervisor reads the `apps` folder (`AppsLauncherManager.refreshAvailableAppsList`).

[3] Regression tests: `PackageNameTest` and `AppsLauncherManagerLinuxTest` (`nmf-services-impl`). The latter starts a real shell in a directory whose name holds shell characters, and checks that the start script runs in that directory.
