Security Advisories
========================

This file lists known security vulnerabilities in the NanoSat MO Framework and their status, both fixed and open.
Each entry follows the structure of a GitHub security advisory, so that it can be published as one.

Only vulnerabilities that are already public, or already fixed, are listed here.
To report a new, undisclosed vulnerability, see [SECURITY.md](../SECURITY.md);
it must be reported privately, not added to this file.

### Index

| ID | Title | Status | Affected | Fixed in | GHSA / CVE |
|---|---|---|---|---|---|
| [NMF-2026-001](#nmf-2026-001-unsafe-deserialization-in-the-spacecraft-simulator) | Unsafe deserialization in the spacecraft simulator | Fixed | < 5.0 | [5.0](https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0) | CVE pending |
| [NMF-2026-002](#nmf-2026-002-unsafe-deserialization-of-parameter-and-action-values) | Unsafe deserialization of parameter and action values | Fixed | < 6.0 | 6.0 | CVE pending |
| [NMF-2026-003](#nmf-2026-003-supervisor-and-apps-run-with-excessive-privileges) | Supervisor and apps run with excessive privileges | Fixed | < 3.0.0 | 3.0.0, [5.0](https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0) | — |
| [NMF-2026-004](#nmf-2026-004-os-command-injection-through-the-app-name-in-the-linux-launcher) | OS command injection through the app name in the Linux launcher | Fixed | < 6.0 | 6.0 | — |
| [NMF-2026-005](#nmf-2026-005-path-traversal-zip-slip-when-installing-or-removing-nmf-packages) | Path traversal (Zip Slip) when installing or removing NMF packages | Fixed | < 2.1.0 | 2.1.0 | — |
| [NMF-2026-006](#nmf-2026-006-packages-can-write-or-delete-files-outside-their-own-area) | Packages can write or delete files outside their own area | Fixed | < 6.0 | 6.0 | — |
| [NMF-2026-007](#nmf-2026-007-default-password-and-root-equivalent-sudo-rights-for-the-nmf-admin-account) | Default password and root-equivalent sudo rights for the nmf-admin account | Fixed | >= 5.0, < 6.0 | 6.0 | — |
| [NMF-2026-008](#nmf-2026-008-os-command-injection-through-the-launch-values-of-an-app-package) | OS command injection through the launch values of an App package | Fixed | < 6.0 | 6.0 | GHSA-36jr-c3j2-8jhw |
| [NMF-2026-009](#nmf-2026-009-os-command-injection-through-the-artificial-intelligence-service) | OS command injection through the Artificial Intelligence service | Fixed | >= 3.0.0, < 6.0 | 6.0 | — |
| [NMF-2026-010](#nmf-2026-010-unauthenticated-command-execution-through-the-commandexecutor-service) | Unauthenticated command execution through the CommandExecutor service | Fixed | >= 2.0.0, < 6.0 | 6.0 | — |
| [NMF-2026-011](#nmf-2026-011-an-app-can-take-over-the-supervisors-entry-in-the-directory-service) | An App can take over the Supervisor's entry in the Directory service | Fixed | < 6.0 | 6.0 | — |
| [NMF-2026-012](#nmf-2026-012-a-package-with-the-name-of-an-installed-app-takes-it-over) | A package with the name of an installed App takes it over | Fixed | < 6.0 | 6.0 | — |
| [NMF-2026-013](#nmf-2026-013-an-unknown-or-missing-apps-isolation-setting-runs-apps-without-isolation) | An unknown or missing apps-isolation setting runs Apps without isolation | Fixed | >= 5.0, < 6.0 | 6.0 | — |

---

### NMF-2026-001: Unsafe deserialization in the spacecraft simulator

| Field | Value |
|---|---|
| GHSA / CVE | GHSA not published. CVE requested from MITRE by the reporter, ID not yet assigned. |
| Ecosystem | Maven |
| Package | `int.esa.nmf.simulator:spacecraft-simulator` |
| Affected versions | < 5.0 |
| Patched versions | 5.0 (artifact renamed to `int.esa.nmf.simulator:cubesat-spacecraft-simulator`) |
| Reported | 2026-07-07 |
| Fixed | 2026-07-10 (commits 4e2a55b and 19f1536), released in 5.0 on 2026-07-21 |
| Severity | To be assessed |
| Weaknesses | CWE-502: Deserialization of Untrusted Data; CWE-1327: Binding to an Unrestricted IP Address |
| Credits | To be confirmed |

#### Impact

The spacecraft simulator exchanges serialized Java objects over TCP between its server (port 11111 by default) and its clients,
such as the simulator GUI.
Up to version 4.0, both sides deserialized the received objects without an `ObjectInputFilter`,
and the server listened on all network interfaces, regardless of the configured listen address.
Configuring a loopback address therefore did not restrict exposure.

An unauthenticated attacker who can reach the server port can send crafted serialized objects,
which the server deserializes without restriction.
This allows denial of service through object graphs that exhaust memory or CPU, regardless of the classpath,
and remote code execution if a known deserialization gadget chain is on the simulator's classpath.
A malicious server can do the same to a client that connects to it.

The simulator is a development and test tool. It does not run on board a spacecraft.

#### Patches

Fixed in version 5.0, by commits 4e2a55b and 19f1536:

* The server accepts only the command types that clients send, and the JDK scalar and collection types they contain.
  The client accepts only the simulator's own classes and JDK types (`SimulatorSerialFilter`).
* Both sides limit the depth of the received object graphs and the length of any single array.
* The server binds to the configured listen address instead of all network interfaces.

Version 5.0 also limited the number of references and the number of bytes read.
Version 5.1 removed these two limits (commit e19b450): the JDK counts them over the whole connection,
so they rejected legitimate traffic on long-lived connections.

The reporter verified the fix with the original proof of concept.

> Note: Since 6.0, the simulator and its GUI no longer use Java serialization on this socket.
> They exchange their messages as JSON, one message per line, of a fixed set of types (`SimulatorMessages`),
> so no Java object is deserialized at all and `SimulatorSerialFilter` was removed.

#### Workarounds

Do not expose the simulator's TCP port to an untrusted network.
Restrict access to the port with a firewall, or run the simulator on an isolated host.

#### References

[1] Fix:
commits [4e2a55b](https://github.com/esa/nanosat-mo-framework/commit/4e2a55b24c0a4ff59a1c2c972d6231986c3a68b4) and [19f1536](https://github.com/esa/nanosat-mo-framework/commit/19f15364f9809f6c29e70877c18d6c65f521f00e)

[2] Change to the limits in 5.1:
commit [e19b450](https://github.com/esa/nanosat-mo-framework/commit/e19b4501f2f855a115791112422c4d62a67925aa)

[3] Release 5.0: https://github.com/esa/nanosat-mo-framework/releases/tag/release-5.0

---

### NMF-2026-002: Unsafe deserialization of parameter and action values

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published. CVE requested, ID not yet assigned. |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-composites` (the deserialization helper is `HelperAttributes` in the MO library `int.esa.ccsds.mo:api-area001-v003-mal`) |
| Affected versions | < 6.0 (every release, from the oldest tagged one, 1.2.0) |
| Fixed in | 6.0 |
| Reported | Thales, Hack CYSAT 2023 |
| Severity | High (unauthenticated, remote; full remote code execution where a gadget chain is on the classpath) |
| Weaknesses | CWE-502: Deserialization of Untrusted Data |

#### Impact

An application built on the NMF Monitor and Control adapters deserializes attacker-controlled bytes with `java.io.ObjectInputStream.readObject()` and no allow-list or `ObjectInputFilter`.

When a parameter or action value arrives as a MAL `Blob`,
the adapter passes its raw bytes straight to `HelperAttributes.blobAttribute2serialObject()`, which calls `readObject()` on them.
The bytes are controlled by the remote MAL peer,
so the peer chooses which classes are instantiated and which deserialization callbacks (`readObject`, `readResolve`) run in the victim process.
MO access control is optional and off by default, so no credentials are required unless a deployment adds them.

The sinks are:

* Provider side:
  `ParameterProviderServiceImpl.setValue` → `ParameterManager` → `SimpleMonitorAndControlAdapter.onSetValue` (and `actionArrived`) → `HelperAttributes.blobAttribute2serialObject`.
  A remote consumer that sets a parameter raw value of type `Blob` reaches it.
* Consumer side: `MOAdapterImpl.monitorValueNotifyReceived`.
  A rogue or compromised provider pushing a parameter notification reaches it on the operator's host.
  One example, `camera-acquisitor-system-ground`, has Spring on its classpath, which is known to carry deserialization gadget chains.

The minimum impact, with no gadget chain,
is the forced instantiation of arbitrary classpath classes and the execution of their deserialization callbacks.
The maximum impact, with a gadget chain, is remote code execution and full compromise of the affected process.
Thales demonstrated this at Hack CYSAT 2023 as a covert channel:
an app with no overtly malicious code in its submitted binary received a crafted `Blob` parameter and ran arbitrary code on board.

#### Workarounds

* Do not define `Blob` parameters or actions whose values are deserialized, and do not rely on the framework's automatic object serialization.
* Enable MO access control so that only authorised peers can set values or push notifications.
* Keep providers and consumers on a trusted network, and keep known gadget-bearing libraries off the classpath.

#### Fix

The automatic serialization of objects into, and deserialization of objects from,
a `Blob` was removed from the Monitor and Control adapters (`SimpleMonitorAndControlAdapter`, `NMFProvider`),
from the consumer adapter (`MOAdapterImpl`), and from the Consumer Test Tool.
Parameter and action values are now restricted to the MO attribute types:
a value that is not an MO attribute type is rejected with a logged error instead of being serialized,
and an incoming `Blob` is passed through as bytes and never deserialized.
No untrusted bytes are passed to `ObjectInputStream.readObject()`.
The `serialized-object` example, which relied on the feature, was removed.

The framework no longer calls the unsafe helper `HelperAttributes.blobAttribute2serialObject` of the MO library.
The helper is removed from the MO library in 15.1, and is still present in 15.0 and earlier.

#### References

[1] Thales, "First worldwide hacking demo of an in-orbit satellite", Hack CYSAT 2023: https://www.youtube.com/watch?v=sXGQWLJ8904

[2] Sink (before the fix): `SimpleMonitorAndControlAdapter.onSetValue` / `actionArrived`,
and `MOAdapterImpl.monitorValueNotifyReceived`, calling `HelperAttributes.blobAttribute2serialObject`.

[3] Covered by the regression test `Nmf2026002Test` in this toolkit, which guards against the deserialization being reintroduced.

---

### NMF-2026-003: Supervisor and apps run with excessive privileges

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | The supervisor and App launcher (`int.esa.nmf.core:nmf-services-impl`), and the deployment generated by `int.esa.nmf.core:nmf-linux-maven-plugin` |
| Affected versions | The mainline before the 3.0.0 consolidation, as deployed on OPS-SAT (the Supervisor ran as root, and NMF itself did not isolate apps). Phi-Sat-2, on its own branch, was not affected. |
| Fixed in | 3.0.0 (the privilege separation consolidated into the mainline); generalized into isolation modes in 5.0 |
| Reported | deadf00d, Hack CYSAT 2022; Thales, Hack CYSAT 2023 |
| Severity | High (privilege escalation to full control of the platform) |
| Weaknesses | CWE-250: Execution with Unnecessary Privileges; CWE-269: Improper Privilege Management |

#### Impact

On OPS-SAT, the NMF supervisor ran as root, and the apps it managed ran without isolation, under the same privileges.
The supervisor is the gatekeeper between the apps and the platform (the sensors and actuators),
so any code that ran in the supervisor, or in any app, had root over the whole on-board computer.

This turns a foothold into full control.
Combined with the unsafe deserialization of parameter values ([NMF-2026-002](#nmf-2026-002-unsafe-deserialization-of-parameter-and-action-values)),
an unprivileged, innocuous-looking app could reach remote code execution in the supervisor and so run as root.
Thales demonstrated this chain at Hack CYSAT 2023.
They escalated further through an OPS-SAT-specific root daemon on the CAN bus, which is not part of NMF.

Phi-Sat-2 was not affected.
It already ran with the privilege separation that was later consolidated at version 3.0.0
— the supervisor as the dedicated unprivileged `nmf-admin` user,
and each app as its own unprivileged Linux user — so compromising an app, or the supervisor, did not grant root over the platform.

#### Fix

The framework no longer requires root, and gives the supervisor and each app their own unprivileged user account:

* Phi-Sat-2 mission: the privilege separation was first developed here.
  The supervisor runs as a dedicated unprivileged user, `nmf-admin`, instead of root,
  and each app runs as its own dedicated unprivileged Linux user (`app-<name>` in the `nmf-apps` group).
  The supervisor creates the app's user when the app is installed and starts the app through `sudo`.
  It was implemented directly in the framework code and applied unconditionally on Linux.
  Phi-Sat-2 ran from its own branch, tagged (`phi-sat-2-cdr`, `phi-sat-2-readiness-review`) but never released as a proper version.

* Version 3.0.0: this privilege separation was consolidated into a common baseline,
  so that the OPS-SAT and Phi-Sat-2 missions, and all later development, build on it.

* Version 5.0: the per-app isolation was generalized into selectable modes:
  * `linux-userspace`: the original approach — each app as its own unprivileged user, the supervisor as `nmf-admin`.
  * `bubblewrap`: each app inside a bubblewrap sandbox.
  * `docker-containers`: each app inside a Docker container.
  * `none`: no isolation.

  The mode is chosen at mission-development time:
  the mission sets the `nmf-linux-maven-plugin`'s `appsIsolation` parameter,
  and the plugin bakes it into the generated bootloader configuration and the supervisor's start script,
  which applies it at startup (as the `esa.mo.nmf.packagemanager.appsIsolation` property).
  It is a property of the built mission, not a setting changed during operations.
  The plugin also generates `setup_linux_userspace.sh`,
  which creates the `nmf-admin` user and the per-app users and groups,
  so the setup is produced by the framework rather than hardcoded in the framework code as it had been for Phi-Sat-2.

An app's storage directory is owned by the app and the `nmf-admin` group with `rwxrwx---`, so it is not world-accessible.

#### Mission design considerations

The framework provides the mechanisms;
choosing the privilege model is a mission-design decision, made when the mission is built,
and it depends on the mission's concept of operations.
A mission that runs third-party or otherwise untrusted apps — as OPS-SAT did with external experiments
— should run the supervisor as a dedicated unprivileged user and build with an isolation mode (`linux-userspace`, `bubblewrap` or `docker-containers`) rather than the default `none`.
A mission that runs only apps the designer controls, with no external access, may not need app isolation.

#### References

[1] deadf00d, "How I hacked an ESA's experimental satellite", Hack CYSAT 2022, published 2022-04-04:
https://www.deadf00d.com/post/how-to-hack-an-esa-experimental-satellite.html

[2] Thales, "First worldwide hacking demo of an in-orbit satellite", Hack CYSAT 2023: https://www.youtube.com/watch?v=sXGQWLJ8904

[3] App isolation: `AppsIsolationMode` and the `AppsLauncherManager` variants (Linux, Bubblewrap, Docker) in `nmf-services-impl`;
the `appsIsolation` parameter of the `nmf-linux-maven-plugin` (`generate-filesystem`) and the `setup_linux_userspace.sh` it generates.

[4] Phi-Sat-2 milestone tags:
[`phi-sat-2-cdr`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-cdr) and [`phi-sat-2-readiness-review`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-readiness-review).

[5] Regression tests: the per-mode launch-command confinement in `AppsLauncherManagerLinuxTest`,
`AppsLauncherManagerBubblewrapTest` and `AppsLauncherManagerDockerTest` (`nmf-services-impl`);
the generation of the selected isolation mode in `BootloaderGeneratorTest` (`nmf-linux-maven-plugin`);
and the behavioural `Nmf2026003Test` in this toolkit,
which runs the real barebone Supervisor and has it install (PackageManagement) and run (AppsLauncher) the real `privilege-probe` app from its `.nmfpackage`,
then compares the modes:
under linux-userspace the Supervisor runs the app as its own unprivileged user and it can read neither the supervisor's nor root's files,
while under none the app runs as the Supervisor's own user and shares its files (and the Supervisor refuses to run as root).

---

### NMF-2026-004: OS command injection through the app name in the Linux launcher

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl` (in earlier versions, the Software Management services module) |
| Affected versions | < 6.0 |
| Fixed in | 6.0 |
| Reported | deadf00d, Hack CYSAT 2022; the receipt path, in a later private report; the App directory outside the NMF root and the uninstall path, Mari ([mpiirstatebuilding](https://github.com/mpiirstatebuilding)), reported privately |
| Severity | Low (requires the ability to install and run an app, which already runs code of the installer's choosing with the same privileges) |
| Weaknesses | CWE-78: Improper Neutralization of Special Elements used in an OS Command; CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal'); CWE-20: Improper Input Validation |

#### Impact

On Linux, the Supervisor starts an app by passing a single command string to a shell: `/bin/sh -c`,
or `sudo su - app-<name> -c` under linux-userspace isolation.
The string is `cd <app directory>; <environment> ./start_app.sh`, and the app directory ends with the app's name.
The name comes from the `info.name` field of the package metadata, and nothing checked it,
at install or when the Supervisor read the `apps` folder.
Characters such as `;`, `&`,
`|` or `$` in a package name were therefore acted on by the shell when the app was started,
so a package could run commands of its choosing in place of, or before, its start script.

To exploit this, an attacker must place a package in the Supervisor's `packages` folder, install it and run it.
Installing and running an app already executes code of the installer's choosing,
so the injected commands gain nothing over what the app's own code can do.
They run with the same privileges:

* Without isolation (`none`), apps already run as the Supervisor's user.
* Under linux-userspace isolation, the whole string runs inside `su - app-<name>`,
  so the injected commands run as the app's own unprivileged user, not as the Supervisor.
  The name must also be accepted as a Linux user name first, which `adduser` refuses on Debian-based systems.
* The Bubblewrap, Docker and Windows launchers pass the app directory as a separate argument, not through a shell, and are not affected.

The flaw is therefore a robustness and defence-in-depth issue rather than a privilege escalation.

The name is also the file name of the package's receipt, `<name>.receipt`, which the Supervisor writes in `etc/installations-tracker` when the package is installed,
for every type of package, a dependency included.
A name such as `../../apps/hello-world` therefore put the receipt outside that folder, at `apps/hello-world.receipt`,
and a name that leaves the folder and comes back to it, such as `../installations-tracker/hello-world`,
overwrote the receipt of another package, so that the Supervisor took that package to be installed as the forged receipt described it.
An update reads the old receipt and removes the files it lists,
but a package could already have files outside its own area removed through its own file list
(see [NMF-2026-006](#nmf-2026-006-packages-can-write-or-delete-files-outside-their-own-area)),
so the forged receipt gains nothing more.

For an App, the name is also the name of its directory, `apps/<name>`, which install creates,
so a name that leaves the `apps` folder created directories elsewhere, outside the NMF root as well:
on 5.1, a package named `../../../../tmp/nmf-escape` created `/tmp/nmf-escape`.
Once the receipts folder existed, which any earlier install creates,
its receipt was written outside the NMF root too, at `/tmp/nmf-escape.receipt`,
with the content of the package's own metadata.
The start script and the properties files of such an App were not written, and the install was reported as successful.
With that receipt in place, an uninstall of the package would delete `start_app.sh`, `provider.properties` and `transport.properties`
from the directory that the name leads to.
This happens before any isolation mode applies, as the Supervisor installs and uninstalls packages itself.

The command string above was used from the 2.0.0 pre-release onwards.
The earlier OPS-SAT flight and 2.0.0 test builds started an app with `su - <user> -m -c './start_<name>.sh'` when the app ran under its own user,
and with `/bin/sh ./start_<name>.sh` otherwise.
Only the first form passed the name through a shell;
there, a `'` in the name ended the quoting. 1.2.0 ran the app's own command line without a shell and was not affected.

#### Workarounds

* Install only packages from trusted sources.
  This is already necessary, because installing and running an app executes its code.
* Run apps under an isolation mode, so that each app, and anything it runs, is confined to its own user or sandbox.

#### Fix

A package name must now be one plain file name: letters, digits, `.`, `_`, `+` and `-`,
starting with a letter or a digit, at most 128 characters.
The check is made when a package file is read, so install, upgrade and uninstall all refuse a package with any other name.
Install and update refuse such a package with the `Invalid Argument` error of the COM area, whose reason names the package file and says why it is not valid;
nothing is written.
As the name can no longer hold a path, the receipt of a package is always written in `etc/installations-tracker`, under the package's own name.
The Supervisor also ignores a folder in the `apps` folder whose name is not a valid app name.

The Linux launcher now quotes the app directory and each environment value,
so that each one reaches the shell as a single word whatever characters it holds.
It also runs the start script only if changing to the app directory succeeded,
so a `su -` login never runs a script from the user's home directory instead.

#### References

[1] deadf00d, "How I hacked an ESA's experimental satellite", Hack CYSAT 2022, published 2022-04-04:
https://www.deadf00d.com/post/how-to-hack-an-esa-experimental-satellite.html

[2] Command assembly: `AppsLauncherManagerLinux.assembleCommand` in `nmf-services-impl`;
in the OPS-SAT era, `AppsLauncherManager.assembleAppLauncherCommand`.

[3] Name check: `Metadata.isValidPackageName`,
applied in `Metadata.parseZipFile` and when the Supervisor reads the `apps` folder (`AppsLauncherManager.refreshAvailableAppsList`).

[4] Regression tests: `PackageNameTest` and `AppsLauncherManagerLinuxTest` (`nmf-services-impl`).
The latter starts a real shell in a directory whose name holds shell characters, and checks that the start script runs in that directory.
`AppPackageConfinementTest` (`nmf-services-impl`) installs dependency packages whose names are paths,
and checks that they are refused, write no receipt outside the receipts folder, and overwrite no other receipt.
It also installs an App package, listing no file, whose name leaves the NMF root, after the receipts folder has been created,
and checks that it creates no directory and writes no receipt outside the NMF root; it fails without the name check.
`PackageRefusalTest` (`nmf-core`) checks that install and update refuse a package that is not valid with `Invalid Argument` and the reason.

---

### NMF-2026-005: Path traversal (Zip Slip) when installing or removing NMF packages

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-package-lib` (merged into `int.esa.nmf.core:nmf-services-impl` in 6.0) |
| Affected versions | < 2.1.0, and the Phi-Sat-2 branch before its CDR tag |
| Fixed in | 2.1.0; hardened in 3.0.0 and 5.1 |
| Reported | deadf00d, Hack CYSAT 2022 |
| Severity | Low without app isolation; Medium with app isolation (a package can write or delete files with the Supervisor's privileges) |
| Weaknesses | CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal') |

#### Impact

An NMF package is a ZIP archive whose metadata lists the files it carries, each with a path relative to the NMF root.
When a package is installed or upgraded, the framework writes each file to the NMF root joined with that path;
when a package is uninstalled or upgraded, it deletes the files the package lists in the same way.
Before 2.1.0 the path was not checked, so a package that listed a path such as `../../somewhere/file` wrote,
or on uninstall deleted, a file outside the NMF root.
This is the attack known as Zip Slip.

The files are written and deleted with the Supervisor's privileges.
To exploit it, an attacker must get a crafted package installed or uninstalled:

* Without app isolation,
  as on OPS-SAT where the Supervisor ran as root ([NMF-2026-003](#nmf-2026-003-supervisor-and-apps-run-with-excessive-privileges)),
  an installed app already runs with the Supervisor's privileges, so the traversal gains little over what the app's own code can do.
* With app isolation, as on the Phi-Sat-2 branch,
  apps run as their own unprivileged users while packages are still installed by the Supervisor.
  There, the traversal let an app's package write or delete files with the Supervisor's privileges, outside the app's own account.

#### Workarounds

* Install only packages from trusted sources.

#### Fix

* 2.1.0: a `..` in a file path was replaced with a character that no file name may contain, so writing or deleting such a file failed.
* The Phi-Sat-2 branch (by its CDR tag), and then 3.0.0 for all missions: a file path containing `..` is refused.
  From 4.0, the framework also checks that the resolved file lies inside the NMF root.
* 5.1: each path is taken apart at its separators,
  and every piece must be a plain file name before it is appended to the NMF root (`HelperNMFPackage.resolveInside`),
  both when files are written and when they are deleted.
  Names made only of dots, which Windows can read as the parent folder, are refused,
  and harmless names that merely contain `..`, such as `a..b`, are no longer rejected.

#### References

[1] deadf00d, "How I hacked an ESA's experimental satellite", Hack CYSAT 2022, published 2022-04-04:
https://www.deadf00d.com/post/how-to-hack-an-esa-experimental-satellite.html

[2] Writing and deleting package files: `NMFPackage.extractFiles` and `NMFPackageManager.removeFiles`,
both through `HelperNMFPackage.resolveInside` (`nmf-services-impl`).
Before the fix, `NMFPackageManager.installFiles` and `NMFPackageManager.removeFiles`.

[3] Phi-Sat-2 tags:
[`phi-sat-2-readiness-review`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-readiness-review) (affected) and [`phi-sat-2-cdr`](https://github.com/esa/nanosat-mo-framework/releases/tag/phi-sat-2-cdr) (fixed).

[4] Regression test: `NMFPackageZipSlipTest` (`nmf-services-impl`),
which holds that every way of writing a path that leaves the folder is refused,
and that the paths of real files still arrive where they belong.

---

### NMF-2026-006: Packages can write or delete files outside their own area

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl` (in earlier versions, `int.esa.nmf.core:nmf-package-lib`) |
| Affected versions | < 6.0 |
| Fixed in | 6.0 |
| Reported | Found by the NMF maintainers, while reviewing [NMF-2026-005](#nmf-2026-005-path-traversal-zip-slip-when-installing-or-removing-nmf-packages) |
| Severity | Low without app isolation; Medium with app isolation (an App's package can have code run as the Supervisor) |
| Weaknesses | CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal'); CWE-59: Improper Link Resolution Before File Access ('Link Following') |

#### Impact

Since the fix of [NMF-2026-005](#nmf-2026-005-path-traversal-zip-slip-when-installing-or-removing-nmf-packages),
every path that a package lists must lie under the NMF root.
Nothing, however, tied a package to the part of the root that belongs to its type.
An App package could therefore add or replace files anywhere under the NMF root except the factory baseline:
in another App's folder, in the shared libraries, in the bootloader's configuration,
or in the directories of the running software baseline (`jars-nmf/<version>` and `jars-mission/<version>`).
In the last case the framework also regenerated that directory's checksums,
as it does for a real baseline package,
so the bootloader's integrity check passed and the Supervisor ran the added or replaced code at its next start.
Uninstalling or updating such a package could likewise delete files in those places.
Writing and deleting also followed symbolic links, so a link placed inside a package's area could send a later package's files elsewhere.

The files are written and deleted with the Supervisor's privileges.
To exploit this, an attacker must get a crafted package installed, updated or uninstalled:

* Without app isolation, apps already run as the Supervisor's user, so this gains little over what the app's own code can do.
* With app isolation, apps run as their own unprivileged users, but their packages are installed by the Supervisor.
  There, an App's package could have code run as the Supervisor, or change the bootloader's configuration,
  and so bypass the isolation introduced by [NMF-2026-003](#nmf-2026-003-supervisor-and-apps-run-with-excessive-privileges).

#### Workarounds

* Install only packages from trusted sources.
* Before installing a package, check that every path in its `package-metadata.properties` lies inside the area of its type, listed below.

#### Fix

Each type of package now has an area of its own, which is the layout the package builder already gave it:

| Type | Area |
|---|---|
| `app` | `apps/<name>/` |
| `dependency` | `jars-shared-libraries/` |
| `java` | `java/` |
| `nmf` | `jars-nmf/<version>/` |
| `mission` | `jars-mission/<version>/` |

A package that lists any file outside its area is refused as a whole, before anything is written or deleted,
when it is installed, updated or uninstalled.
A file is also refused if it would be reached through a symbolic link inside the area.
Packages of type `delta` are refused, because they have no area of their own: delta updates are not implemented yet.
Only baseline packages (`nmf`, `mission` and `java`) refresh the checksums of the baseline directories.

#### References

[1] Where a package's files may go: `HelperNMFPackage.resolveInsideArea` and `HelperNMFPackage.areaOf`,
applied by `NMFPackage.placeFiles` and by `NMFPackageManager` when it removes files (`nmf-services-impl`).

[2] Regression test: `AppPackageConfinementTest` (`nmf-services-impl`),
which installs and uninstalls hand-made packages that reach into the framework's baseline,
into another App's folder and through a symbolic link,
and checks that each is refused and leaves no file written or deleted,
while ordinary App and framework packages still install where they belong.

---

### NMF-2026-007: Default password and root-equivalent sudo rights for the nmf-admin account

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-linux-maven-plugin` (the generated `setup_linux_userspace.sh`) and `int.esa.nmf.core:nmf-services-impl` |
| Affected versions | >= 5.0, < 6.0 |
| Fixed in | 6.0 |
| Reported | Reported privately |
| Severity | High where the host accepts password logins, at a console or over SSH; Low otherwise (requires code already running as the Supervisor) |
| Weaknesses | CWE-1392: Use of Default Credentials; CWE-798: Use of Hard-coded Credentials; CWE-269: Improper Privilege Management |

#### Impact

Under linux-userspace isolation, the Supervisor runs as the dedicated `nmf-admin` account,
which the generated `setup_linux_userspace.sh` creates during the initial hardware deployment.
The script has done so since 5.0, when it was brought over from the Raspberry Pi mission.
Earlier versions did not ship a provisioning script.

The script gave `nmf-admin` the fixed password `raspberry`, published in the repository,
and offered no step to change it.
Anyone who could log in to the host, at a console or over SSH with password authentication,
could therefore log in as `nmf-admin`.

The script also gave `nmf-admin` sudo rights, without a password, that were meant to let it manage the accounts of the apps.
They were not limited to that:
`chpasswd`, `adduser`, `deluser` and `chgrp` accepted any argument,
and the `chmod` rule for `/home/*` also matched paths that leave `/home` through `..`.
With them, `nmf-admin` could set the password of any account, join any group, delete any account,
or make any root-owned file accessible to itself.
`nmf-admin` was therefore equivalent to root.

Together, the two gave root to anyone who could log in to the host.
Apps were not affected directly:
under linux-userspace each app runs as its own user, which has no sudo rights.

#### Workarounds

* Lock the account: `sudo passwd -l nmf-admin`.
* Remove the `chpasswd`, `adduser`, `deluser`, `chgrp` and `chmod` lines from `/etc/sudoers.d/nmf-admin`,
  or provision the host again with the `setup_linux_userspace.sh` of 6.0, which installs the helper described below.
  Without those rules, the Supervisor of an earlier version can no longer create or delete the accounts of apps.
* Disable password authentication over SSH on the host.

#### Fix

The `nmf-admin` account no longer has a password and is locked, so nobody can log in as it.
Root starts the Supervisor as `nmf-admin`, which needs no password.
Operators who need a shell as `nmf-admin` use their own account and `sudo -iu nmf-admin`.

`nmf-admin` may now run only two commands as root:

* `su - app-<name>`, to start an app as its own user;
* `/usr/local/sbin/nmf-app-account`, a root-owned helper that the setup script installs.
  It creates and deletes the account of an app,
  and gives the files of the app's own installation and log directories to the app's group.
  It refuses any account that is not `app-<name>`, with the same name rule as a package,
  and any other directory.
  It changes the group only of files that `nmf-admin` or the app owns, and does not follow links.

The Supervisor calls the helper in place of the generic commands.
A second run of the setup script, as needed to provision an existing host again, no longer fails on Debian-based systems.

#### References

[1] Provisioning: `setup_linux_userspace.sh` in `nmf-linux-maven-plugin`,
generated by `BootloaderGenerator` when the linux-userspace isolation mode is selected.

[2] Use of the helper: `LinuxUsersGroups.addAppUser`, `LinuxUsersGroups.deleteAppUser` and `LinuxUsersGroups.giveToAppGroup`,
called by `NMFPackageManager` (`nmf-services-impl`).

[3] Regression tests: `Nmf2026007Test` (`security-toolkit`),
which provisions a container with the generated script and checks, as `nmf-admin`,
that the published password does not open the account, that the account is locked,
and that each of the sudo rights above is refused;
`Nmf2026003Test`, which checks that the Supervisor still creates the account of a real app and runs it as that user;
and `BootloaderGeneratorTest` (`nmf-linux-maven-plugin`), which checks the generated script.

---

### NMF-2026-008: OS command injection through the launch values of an App package

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | GHSA-36jr-c3j2-8jhw (not published) |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl` (in earlier versions, `int.esa.nmf.core:nmf-package-lib`) |
| Affected versions | < 6.0 |
| Fixed in | 6.0 |
| Reported | freedomfoxvare, through a GitHub security advisory |
| Severity | Low (requires the ability to install and run an app, which already runs code of the installer's choosing with the same privileges) |
| Weaknesses | CWE-78: Improper Neutralization of Special Elements used in an OS Command; CWE-22: Improper Limitation of a Pathname to a Restricted Directory (Path Traversal); CWE-20: Improper Input Validation |

#### Impact

When an App is installed, the framework generates its start script, `start_app.sh` (`start_app.bat` on Windows),
from the values in the package's `package-metadata.properties`:
the main class (`pack.app.mainclass`), the main jar (`pack.app.mainjar`),
the heap sizes (`pack.app.maxheap` and `pack.app.minheap`) and the dependencies (`pack.app.dependencies`).
Each value was written into a shell assignment, such as `MAIN_CLASS=<value>`,
without quotes and without any check.
When the App was started, the shell acted on any shell syntax in a value,
so a package could run commands of its choosing, for example with `pack.app.mainclass=$(<command>)`.
The main jar and the dependencies could also be any path,
so a package could make its App load a jar from outside its own directory,
such as the jar of another installed App, with `pack.app.mainjar=../hello-world/hello-world-5.1.jar`.
The package checksums do not prevent this, because whoever makes the package also computes them.

To exploit this, an attacker must get a package installed and the App run.
Installing and running an App already executes code of the installer's choosing,
and the start script runs with the App's own privileges,
so the injected commands gain nothing over what the App's own code can do:

* Without isolation (`none`), apps already run as the Supervisor's user.
* Under linux-userspace isolation, the start script runs as the App's own unprivileged user.
* Under Bubblewrap and Docker isolation, the start script runs inside the App's sandbox or container.

The flaw is therefore a robustness and defence-in-depth issue rather than a privilege escalation,
like [NMF-2026-004](#nmf-2026-004-os-command-injection-through-the-app-name-in-the-linux-launcher).
The start script has been generated from the package's values since the 2.0.0 pre-release.

#### Workarounds

* Install only packages from trusted sources.
  This is already necessary, because installing and running an App executes its code.
* Before installing a package, check that its main class is a Java class name,
  its jar and dependencies are plain file names, and its heap sizes are numbers with an optional unit.
* Run apps under an isolation mode, so that each App, and anything it runs, is confined to its own user or sandbox.

#### Fix

The launch values of an App are now checked when a package file is read,
so install, upgrade and uninstall all refuse a package with any other values:

* the main class must be a Java class name: Java identifiers separated by dots;
* the main jar and each dependency must be one plain file name:
  letters, digits, `.`, `_`, `+` and `-`, starting with a letter or a digit,
  so that no path, absolute or relative, can lead outside the App's own directory;
* each heap size must be a number with an optional `k`, `m`, `g` or `t` unit.

The generated `start_app.sh` also quotes every value it assigns,
so that the shell takes each one as it is, whatever characters it holds.

#### References

[1] Script generation: `AuxFilesGenerator.generateLinuxStartAppScript` and `AuxFilesGenerator.generateWindowsStartAppScript` in `nmf-services-impl`;
before 3.0.0, `HelperNMFPackage.generateLinuxStartAppScript` in `nmf-package-lib`.

[2] Value check: `MetadataApp.checkLaunchValues`, applied in `Metadata.parseZipFile`.

[3] Regression test: `StartAppScriptTest` (`nmf-services-impl`),
which checks that packages with shell syntax in each launch value are refused,
and generates the start script from such values, runs it with a real `/bin/sh`,
and checks that none of their commands runs and that `java` receives the main class as one argument;
it also checks that packages whose main jar or dependencies point outside the App's directory are refused.

---

### NMF-2026-009: OS command injection through the Artificial Intelligence service

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl` (in earlier versions, the platform services module) |
| Affected versions | >= 3.0.0, < 6.0 |
| Fixed in | 6.0 |
| Reported | Reported privately |
| Severity | High (a consumer of the AI service runs commands as the platform-services process) |
| Weaknesses | CWE-78: Improper Neutralization of Special Elements used in an OS Command; CWE-20: Improper Input Validation |

#### Impact

The Artificial Intelligence service runs inference by handing a command to a shell.
Its Movidius adapter built that command by joining fixed text with the model path and the tile paths,
and `ShellCommander` ran the result through `sh -c`, which parses the whole string.
The two service operations that take those paths checked almost nothing:
`setModel` only checked that the model path ended in `.xml`,
and `doInference` only checked that the input path existed.

A consumer of the AI service could therefore put shell syntax in a path and have the shell run it.
For example, a model path of `$(<command>)x.xml` ends in `.xml`, so `setModel` accepted it,
and `doInference` then ran `<command>` as part of the inference command.
The commands run in the process that provides the platform services,
with that process's user and access, which is the Supervisor's on a mission that provides the AI service in-process.

Unlike the package flaws [NMF-2026-004](#nmf-2026-004-os-command-injection-through-the-app-name-in-the-linux-launcher)
and [NMF-2026-008](#nmf-2026-008-os-command-injection-through-the-launch-values-of-an-app-package),
this needs no package to be installed:
any consumer allowed to reach the AI service can use it, and the commands run with the platform-services process's privileges, not an App's.

The AI service is provided only by a mission whose platform layer starts it with a Movidius adapter.
In this repository that is the Orekit simulator's provider, which starts the adapter when `python3` is present on the host.
A mission that does not provide the AI service, or provides it with an adapter that does not shell out, is not affected.

The service and its Movidius adapter have been present since 3.0.0.

#### Workarounds

* Do not provide the Artificial Intelligence service on a mission that is reachable by untrusted consumers,
  or disable it in the mission's platform-services provider.
* Turn on MO access control so that only trusted consumers can reach the service.

#### Fix

The service now validates the paths before anything runs, and the adapter no longer lets a path reach the shell as code:

* `setModel` requires the model path to be an existing regular file ending in `.xml`,
  and `doInference` requires the input path to be an existing directory;
  both refuse a path that holds a control character.
* The Movidius adapter runs the inference through an argument vector,
  with each path as its own argument, executed directly rather than spliced into a shell string.
  A fixed one-line script sources the Intel variables file and then replaces itself with that command,
  so the shell never parses a path.
* `ShellCommander` gained an argument-vector form, `runCommand(String[], File)`,
  that executes a command directly without a shell; the string form keeps its warning that its input must not come from outside.

#### References

[1] Service checks: `ArtificialIntelligenceProviderServiceImpl.setModel` and `doInference` in `nmf-services-impl`.

[2] Safe command: `AIMovidiusAdapter.executeInference` (an argument vector) and `ShellCommander.runCommand(String[], File)` in `nmf-services-impl`.

[3] Regression test: `Nmf2026009Test` (`security-toolkit/vulnerability-tests`),
which drives the service and the adapter with a path that would create a marker file if the shell evaluated it,
and checks that no marker appears; it fails on the earlier code.

---

### NMF-2026-010: Unauthenticated command execution through the CommandExecutor service

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl`, `int.esa.nmf.core:nmf-composites` |
| Affected versions | >= 2.0.0, < 6.0 |
| Fixed in | 6.0 |
| Reported | — |
| Severity | Critical (any party that can reach the Supervisor runs shell commands as the Supervisor's user, without credentials) |
| Weaknesses | CWE-306: Missing Authentication for Critical Function; CWE-862: Missing Authorization |

#### Impact

The Supervisor always provides the CommandExecutor service of the Software Management area.
Its `runCommand` operation runs the command it is given through `/bin/sh -c`,
in the Supervisor's process and with the Supervisor's user.

The MAL calls an access control hook on every message,
but when none is configured it falls back to `EchoSecurityManager`, which lets every message through.
The NMF never configured one, so no service of the Supervisor checked who called it.

Anyone who could reach the Supervisor over the network could therefore call `runCommand`
and run any shell command, without presenting any credential.
This was reproduced on the playground environment of 5.0.

Apps could do the same, so the flaw also bypassed every isolation mode:
an App under linux-userspace, Bubblewrap or Docker isolation could run commands as the Supervisor's user, outside its own sandbox.

The CommandExecutor has been part of the Supervisor since the 2.0.0 pre-releases.

#### Workarounds

* Make sure that only ground can reach the Supervisor's MAL ports,
  for example with a firewall on the spacecraft's network.
* Run the Supervisor as an unprivileged user (see [NMF-2026-003](#nmf-2026-003-supervisor-and-apps-run-with-excessive-privileges)),
  so that the commands run without root rights.

These limit the impact but do not stop an App from calling the service.

#### Fix

The NMF now configures its own access control in the MAL, and the Supervisor checks each message it receives:

* Ground presents a credential, `bootloader/ground.credential`,
  which the filesystem generator creates anew for each filesystem and which only the Supervisor may read.
  The CLI, the Consumer Test Tool and the testbeds present it to the Supervisor's Directory only.
* Each App receives its own credential when it is started, revoked when it stops.
  It may call only a baseline of services, plus the permissions it declares in its package and that ground acknowledges in the install or update request.
  The CommandExecutor is not in the baseline.
* A caller without a valid credential may only look up the Directory.
  This holds whatever host, port, area version, service or operation the header of its request names.
* Publishing to the Supervisor's brokers is checked as well.
* Each refused call is stored in the COM Archive as an `UnauthorisedCall` object of the Apps Launcher service.
* The Supervisor keeps its copy of the ground credential in a file on board, and writes each App's credential to a file for that App.
  An App must not read any of these files except its own.
  Under linux-userspace isolation, file permissions keep them apart.
  Under Bubblewrap and Docker isolation, the sandbox now hides them:
  before, it showed the App the whole filesystem read-only, so an App could read them.
  Without isolation (`none`), every App runs as the Supervisor's user and can read them;
  `none` gives no protection between Apps and the Supervisor.

A mission chooses the mode with the `accessControl` parameter of the `nmf-linux-maven-plugin`:
`off`, `audit` or `enforce`.
`enforce` is the default, also when the Supervisor starts without a mode or with one it does not know.
In `audit` mode, calls that would be refused are only logged and archived;
the playground environment and the testbeds use it, so the CommandExecutor still runs commands there.

The ground credential travels in clear, in the authentication field of each MAL message,
so the link between ground and the spacecraft must be encrypted.

#### References

[1] Command execution: `CommandExecutorProviderServiceImpl.runCommand` in `nmf-services-impl`.

[2] Access control: `NMFAccessControl`, `AccessControlAuthority` and `AccessControlMode` in `nmf-services-impl` (package `esa.mo.nmf.accesscontrol`);
set up by `NanoSatMOSupervisor` and `NanoSatMOConnectorImpl` in `nmf-composites`.

[3] Regression tests: `RequestsToTheSupervisorTest` and `AccessControlModeTest` (`nmf-services-impl`),
which check that a request without credentials to the CommandExecutor is refused whatever its header names,
and that a missing or unknown mode is enforced;
`AppSandboxSecretsTest` (`nmf-services-impl`), which checks that a sandboxed App cannot read other credentials;
`AccessControlAuditTest` (`testbed-e2e`);
and `AppPermissionsTest` (`security-toolkit/access-control-tests`),
which runs an App against a Supervisor in audit and in enforce mode and checks each call it makes, including `runCommand`.

---

### NMF-2026-011: An App can take over the Supervisor's entry in the Directory service

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl`, `int.esa.nmf.core:nmf-composites` |
| Affected versions | < 6.0 |
| Fixed in | 6.0 |
| Reported | — |
| Severity | High (an installed App makes ground, and the Apps started after it, talk to it instead of the Supervisor) |
| Weaknesses | CWE-290: Authentication Bypass by Spoofing; CWE-862: Missing Authorization |

#### Impact

Ground and the Apps find the Supervisor through its Directory service:
they look up the provider named `nanosat-mo-supervisor` and connect to the service addresses of that entry.

The `add` operation of the Directory service adds a provider entry under the name it is given.
If an entry with that name already exists, `add` removes it and stores the new one in its place,
whoever made the request.
`remove` likewise removes any entry, given its identifier.
Neither checks that the provider named in the request belongs to the caller.

An App can therefore replace the Supervisor's entry with one that lists its own service addresses:

* An App registers itself under its own name when it starts,
  and that name is the name of its package.
  The package name may be `nanosat-mo-supervisor`,
  so a package of that name takes over the Supervisor's entry just by being run.
* Any running App can also call `add` itself with any provider name.
  The access control of the Supervisor in 6.0 lets every App call the Directory service,
  so, before this fix, this needed no permission there either.

Once the entry is replaced:

* Ground looks up the Supervisor and connects to the App instead.
  The App receives the requests that ground meant for the Supervisor,
  such as install, update and runApp requests, and can answer them with responses of its choosing,
  so that ground sees packages as installed and Apps as running when they are not.
  When the App does not provide a service of the Supervisor, such as Package Management,
  that service is no longer available to ground.
* Every App started afterwards looks up the Supervisor in the same way to reach the Platform services,
  so it receives the App's camera, GPS and other Platform data, and its Platform requests go to the App.
* The Supervisor itself keeps running at its own addresses with its own privileges.
  A client that connects to known Supervisor service addresses, without a look-up, is not affected.

Ground and each App present their credential only to the address of the Supervisor that they were configured with,
so the App does not receive the credential of ground or of other Apps:
it impersonates the Supervisor, but cannot act as ground towards the real one.

To exploit this, an attacker must get an App installed and run, which ground must approve.
In versions before 6.0, the Supervisor checks no caller
(see [NMF-2026-010](#nmf-2026-010-unauthenticated-command-execution-through-the-commandexecutor-service)),
so anyone who can reach the Supervisor over the network can call `add` without installing an App.

This was reproduced on the barebone mission of 6.0 in development, before the fix,
under linux-userspace isolation and with access control enforced,
both with an App that adds an entry under the Supervisor's name and with an App packaged as `nanosat-mo-supervisor`:
while the App ran, ground found the App under the Supervisor's name, and Package Management was no longer available to it.

The ground MO proxy (`GroundMOProxy`) has a related weakness:
it relays as the Supervisor every provider whose name starts with `nanosat-mo-supervisor`,
so an App named, for example, `nanosat-mo-supervisor-x` is relayed to ground as the Supervisor
without replacing any entry.

The Directory service has replaced an entry of the same name since 2017,
and Apps have been registered under the name of their folder, which is the name of their package, since the same year.

#### Workarounds

* Install only packages from trusted sources,
  and refuse a package whose name is `nanosat-mo-supervisor` or starts with it.
* On ground, after looking up the Supervisor, check that the service addresses of its entry
  have the host and port of the Directory service that was looked up:
  the Supervisor provides all its services at the same address as its Directory.

#### Fix

The entries of the Supervisor's Directory service now belong to their providers:

* The Directory service refuses an `add` or a `remove` of an entry that it added for itself to every consumer, in every access control mode.
  The Supervisor adds its own entry when it starts, so a look-up of the Supervisor always leads to the Supervisor.
* When access control is enforced, the Supervisor lets an App add and remove only its own entry,
  the one under the App's name, which the App adds when it starts and removes when it stops.
  It refuses every other `add` and `remove`, from other Apps and from ground alike,
  which never needs to add or remove an entry.
  In `audit` mode, such a call goes through and is logged.
* Each refused or audited call is logged and stored in the COM Archive as an `UnauthorisedCall` object of the Apps Launcher service,
  whose new `entry` field names the entry that the call tried to add or remove.
* A package cannot be installed as an App named `nanosat-mo-supervisor`.
* The ground MO proxy relays as the Supervisor only the provider named exactly `nanosat-mo-supervisor`.

The `add` and `remove` operations of the Directory service return the `Authorisation Fail` error of the MAL for a refused call.

#### References

[1] Directory entries: `DirectoryProviderServiceImpl.add` and `DirectoryProviderServiceImpl.remove` in `nmf-services-impl`;
the check of the caller is `AccessControlAuthority.checkDirectoryEntry`, set up by `NanoSatMOSupervisor` in `nmf-composites`.

[2] Registration of an App: `NanoSatMOConnectorImpl.init` in `nmf-composites`,
which takes the provider name from the App's folder and adds it to the Supervisor's Directory;
its look-up of the Platform services by the name `nanosat-mo-supervisor` is in the same method.

[3] Package names: `Metadata.parseZipFile` in `nmf-services-impl`, which now refuses an App named `nanosat-mo-supervisor`.

[4] Access control: `AccessControlAuthority.Call.isBaseline` in `nmf-services-impl`, which lets every App call the Directory service.

[5] Ground MO proxy: `GroundMOProxy.getRemoteNMSProviderSpecificService` in `nmf-composites`.

[6] Regression test: `Nmf2026011Test` (`security-toolkit/vulnerability-tests`),
which gives a Directory service the Supervisor's entry, as the Supervisor does at start-up,
and checks that a separate consumer, standing for an App, can neither replace that entry with its own addresses nor remove it;
it fails on the earlier code.
`Nmf2026011EndToEndTest` (`security-toolkit/vulnerability-tests`), with the `app-directory-probe` App,
which has the real Supervisor install and run the App under linux-userspace isolation, with access control enforced,
both as `directory-probe` and as `nanosat-mo-supervisor`,
and checks that ground still reaches the Supervisor's Package Management while the App runs,
or that the package named after the Supervisor is not installed,
and that the App can add no entry under the name of another App or under a name that is no App's,
and cannot remove the Supervisor's entry;
it fails on the earlier code.
`DirectoryEntryOwnershipTest` and `PackageNameTest` (`nmf-services-impl`),
which check the rule for each caller and each access control mode, and the reserved name.

---

### NMF-2026-012: A package with the name of an installed App takes it over

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-package-lib`, `int.esa.nmf.core:nmf-services-impl` |
| Affected versions | < 6.0 |
| Fixed in | 6.0 |
| Reported | Mari ([mpiirstatebuilding](https://github.com/mpiirstatebuilding)), reported privately |
| Severity | Medium (a package installed under the name of another App replaces its code and takes over its directory, its storage and, under linux-userspace, its Linux account) |
| Weaknesses | CWE-694: Use of Multiple Resources with Duplicate Identifier; CWE-284: Improper Access Control |

#### Impact

An App is identified by the name that its package declares.
The name gives the App directory (`apps/<name>`), its storage (`.nmf-apps/<name>`), its log directory, its installation receipt and,
under linux-userspace isolation, its Linux account (`app-<name>`).

The `install` operation of the Package Management service refuses a package only when a package with the same name and the same final version is installed.
A package with the name of an installed App and any other version, higher or lower, is installed as if the name were free:

* Its files are written into the directory of the installed App, whose own files stay in place.
* The start script and the provider and transport properties of the App are generated again for the new package,
  so the App runs the code of the new package from then on.
* The receipt of the installed App is replaced by the receipt of the new package, with the permissions that ground acknowledged for it.
* Under linux-userspace isolation, the new package gets the Linux account of the installed App, with the same uid and gid.
* The App is not stopped first, the result is reported as a successful install,
  and no error or warning says that another App was replaced.

The `findPackage` operation compares the whole package, not only its name,
so it reports the new package as not installed while the other App is,
and gives ground no sign of the collision before it approves the install.

The new package therefore takes over everything that belonged to the installed App:
its code, its configuration, the data in its storage and, under linux-userspace, its account and every file that the account owns.
The `update` operation, which is meant for replacing an App, stops it first and removes its files; `install` does neither.

This needs no crafted package.
Two developers who choose the same App name by chance cause it.

To exploit this, an attacker must get a package installed, which ground must approve.
In 6.0, ground approves the SHA-256 of each package file it installs,
but nothing in the approval tells ground that the package replaces another App.
In versions before 6.0, the Supervisor checks no caller
(see [NMF-2026-010](#nmf-2026-010-unauthenticated-command-execution-through-the-commandexecutor-service)),
so anyone who can reach the Supervisor over the network can install a package already on board.

Packages have been refused only when the same name and version are installed since 2.0.0;
the takeover of the Linux account needs linux-userspace isolation, available since 3.0.0.
Mari reproduced it on 6.0 in development, under linux-userspace, also with a package of a lower version than the installed one.
It was also reproduced with the real Package Management service of 6.0 in development, before the fix:
packages of a higher version, of a lower version and of another SNAPSHOT version were each installed over an App of their name,
and the App then started the jar of the new package.

#### Workarounds

* Before installing a package, check with `findPackage` and the list of installed Apps that no installed App has its name.
* Replace an installed App only with `update`, or with `uninstall` followed by `install`.

#### Fix

The `install` operation no longer replaces an installed App:

* A package of an App is refused when an App of its name is installed, whatever the version of either,
  with the `Package Installed` error of the Software Management area, which names the refused packages by their index in the request.
  No file, receipt or account is changed.
* The same SNAPSHOT version may still be installed again over itself, so that a development build can be re-installed.
* An installed App is replaced only with `update`, which stops the App, removes the files of the installed package
  and stores a `PackageUpdated` object with both versions in the COM Archive.
  An update may go to a higher, a lower or the same version.

The rule for the other packages is unchanged: a dependency or a baseline component (NMF, mission or Java runtime) is refused only when its same final version is installed.
The packages of a dependency are named after the library, so the packages of two versions of one library share a name, and two Apps may need different versions of it.

#### References

[1] Installation: `PackageManagementProviderServiceImpl.install` in `nmf-services-impl`,
which refused a package only when `NMFPackageManager.isFinalVersionInstalled` found a receipt with its name and version.
In 6.0, it refuses a package when `NMFPackageManager.replacesInstalledPackage` (`nmf-core`) finds that it would replace an installed one.

[2] Files, receipt and account of an App: `NMFPackageManager.install` in `nmf-package-lib` (`nmf-core` in 6.0),
which writes into `apps/<name>`, replaces the receipt `<name>.receipt` and creates the account `app-<name>`, or reuses it if it exists.

[3] Installed status: `NMFPackageManager.isPackageInstalled`, used by `findPackage`, which compares the whole package with the receipt.

[4] Regression test: `Nmf2026012Test` (`security-toolkit/vulnerability-tests`),
which runs the real Package Management service with the real NMF Package backend on an NMF root of its own,
installs an App, and checks that packages of its name and of a higher, a lower or another SNAPSHOT version are refused
and leave the App's start script, files and receipt unchanged;
it fails on the earlier code.
It also checks that the same SNAPSHOT version can still be installed again and that an update still replaces the App.

---

### NMF-2026-013: An unknown or missing apps-isolation setting runs Apps without isolation

| Field | Value |
|---|---|
| Status | Fixed |
| GHSA / CVE | Not published |
| Ecosystem | Maven |
| Package | `int.esa.nmf.core:nmf-services-impl`, `int.esa.nmf:nmf-linux-maven-plugin` |
| Affected versions | >= 5.0, < 6.0 |
| Fixed in | 6.0 |
| Reported | Mari ([mpiirstatebuilding](https://github.com/mpiirstatebuilding)), reported privately |
| Severity | Low (a wrong or damaged setting silently runs every App as the Supervisor's user; changing the setting on purpose already needs the Supervisor's account) |
| Weaknesses | CWE-636: Not Failing Securely ('Failing Open'); CWE-1188: Initialization of a Resource with an Insecure Default |

#### Impact

The isolation mode of the Apps is the `apps-isolation` setting in `bootloader/config.properties`,
one of `none`, `linux-userspace`, `bubblewrap` and `docker-containers`.
The boot script `start_supervisor.sh` passes it to the Supervisor, which chooses how to start each App from it.

A value that is none of the four, such as a typing error (`linux_userspace`), an empty value or a damaged file,
matches no mode, and the Supervisor starts the Apps as it does without isolation:
each App runs as the Supervisor's own user, with access to everything that user can read and write.
A missing file, or a file without the setting, has the same result, as the boot script then uses `none`.
Nothing is logged, and the Boot Report names only the configuration file,
so this cannot be told apart from a mission that chose `none`.

The other security settings of the same file fail safe:
an unknown `access-control` value enforces access control, and any `require-package-checksum` value other than `false` requires the checksum.

The file is in the `bootloader` directory, which belongs to the Supervisor's account under linux-userspace
and is hidden from the Apps under bubblewrap,
so an App cannot change the setting.
Whoever can change it on purpose already has the Supervisor's account, which gains nothing from running the Apps as itself.
The weakness is that a wrong value weakens the isolation silently, instead of being refused.

The isolation modes, and the setting, were introduced in 5.0.

#### Workarounds

* Check that `apps-isolation` in `bootloader/config.properties` is exactly one of the four modes after every change to the file.
* Check the user that each App runs as.

#### Fix

An isolation mode that is not one of the four no longer runs the Apps without isolation:

* The Supervisor still starts, so that ground can reach it and correct the file, but its Apps Launcher runs no App.
  `runApp` fails with the `Internal` error of the MAL, whose reason names the unknown value and the four modes,
  and the Supervisor logs it when the Apps Launcher starts and on each refused `runApp`.
* A missing file, or a file without the setting, is treated the same way: the boot script no longer uses `none` in its place.
  The filesystem generator of the `nmf-linux-maven-plugin` always writes the setting, so a generated filesystem is not affected.
* A Supervisor started without the setting at all, as from an IDE, still runs its Apps without isolation, as before.
* The Boot Report states the security settings on every boot, on an `INITIALISATION security` line:
  the isolation mode, the access control mode and whether the package checksum is required.
  An unknown or missing isolation mode is also reported on an `INITIALISATION FAIL apps-isolation` line.

The Supervisor does not choose another isolation mode in place of an unknown one:
each mode needs its own set-up, such as the accounts of linux-userspace or the image of docker-containers,
and the Apps installed under one mode are set up for it.

#### References

[1] Isolation mode: `AppsIsolationMode` in `nmf-services-impl`, whose checks all fail for an unknown value,
and `AppsLauncherProviderServiceImpl`, which then starts the Apps with `AppsLauncherManagerLinux`, without a separate user.
In 6.0, `AppsIsolationMode.invalidModeReason` (`nmf-core`) tells an unknown mode apart,
and `AppsLauncherProviderServiceImpl.runApp` and `AppsLauncherManager.startAppProcess` refuse to run an App while the mode is unknown.

[2] Boot script: `start_supervisor.sh` of the `nmf-linux-maven-plugin`, which uses `none` when the setting is missing.

[3] Regression test: `Nmf2026013Test` (`security-toolkit/vulnerability-tests`),
which sets the isolation mode to `linux_userspace`, a typing error for `linux-userspace`,
and checks that `runApp` is refused with the `Internal` error and its reason, and that the Linux launcher starts no App process;
it fails on the earlier code, where the App is started with `/bin/sh -c`, as the Supervisor's user.
`AppsIsolationModeTest` (`nmf-core`) checks which values are valid,
and `BootloaderTest` (`testbeds/testbed-e2e`) checks the `INITIALISATION security` line of the Boot Report.
