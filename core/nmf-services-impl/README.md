NMF Services
============

The implementation of the MO services of the NMF:
* COM services (including Directory and Login)
* M&C services
* Software Management services
* Platform services

It also contains the helper utilities shared by these services, and the NMF Package implementation used by the Package Management service.

NMF Packages
============

An NMF Package is a ZIP archive used to install, uninstall and update software on a spacecraft that runs the NMF. It carries a descriptor with the version of its content and a CRC checksum for each file, so that corrupted content is detected before it is installed. The content does not have to be an NMF App: a package can also hold a dependency, a Java runtime, a mission baseline or an NMF core baseline.

The Package Management service only installs, uninstalls and updates packages that are already on the spacecraft. Transferring them there is left to other software.

The code is in the package `esa.mo.nmf.nmfpackage`. Packages are built at compile time by the `nmf-package-maven-plugin`.
