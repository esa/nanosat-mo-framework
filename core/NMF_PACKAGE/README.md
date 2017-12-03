NMF Package implementation
============

A NMF Package is a digital content distribution package intended to be used to install, uninstall and update applications on a spacecraft system that uses the NanoSat MO Framework. The package shall support versioning, digital signature and file checksums in order to facilitate updates, prevent content tainting and checking for possible data corruption.


An application inside a NMF Package doesn’t necessarily need to be a NMF App and instead it can also be a dedicated or spacecraft-specific application.

The Package Management service does not handle digital distribution of software (transfer of files). It is responsible solely for installing, uninstalling and updating the applications contained inside the packages. In order to transfer the packages to the spacecraft, third-party software for file transfer is necessary.


An adapter for the Package Management service was implemented for the use of NMF Packages with the service. This adapter is able to install, uninstall, and update packages by creating, deleting and updating folders in the file system.



This implementation is done for version 1 of the NMF Package’s descriptor. On a future version, more metadata could be included in the descriptor to provide more information about the content. Suggestions are presented in Chapter 7 of César's dissertation available in the docs folder of the NMF SDK.
The implementation could be also improved by creating a maven plugin hook for the automatic generation of NMF Packages at compilation time. This would dramatically facilitate the generation of NMF Packages for developers using the SDK.


