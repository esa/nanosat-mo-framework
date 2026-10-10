Security Policy
========================

### Supported Versions

Security fixes are made in the version under development and are included in its next release. Fixes for earlier releases may be provided at the discretion of the maintainers, but are not guaranteed. Users are advised to use the latest release.

### Reporting a Vulnerability

Do not report security vulnerabilities through public GitHub issues, discussions or pull requests.

Use GitHub private vulnerability reporting instead: go to the **Security** tab of this repository and click **Report a vulnerability**, or use https://github.com/esa/nanosat-mo-framework/security/advisories/new

Include the following information where possible:

* Affected version or commit
* Affected component (for example: Supervisor, a specific service, NMF Package installation, CLI tool)
* Steps to reproduce, or a proof of concept
* Impact, and the access required to exploit the vulnerability

### Response

The maintainers review every report, acknowledge it, and keep the reporter informed of progress. Once a fix is available, the disclosure date is coordinated with the reporter. Response times depend on the severity and complexity of the issue. Reporters are credited in the published advisory if they agree.

### Scope

All code in this repository is in scope. On-board software has priority: the framework in `core/`, including the services, the Supervisor, the App connector and the NMF Package installation. Simulators, example apps and ground tools (Consumer Test Tool, CLI tool, MCP adapter) follow.

Vulnerabilities in the CCSDS MO stack (MAL, transports, encodings) should be reported to [mo-services-java](https://github.com/esa/mo-services-java/security/advisories/new). Vulnerabilities in other third-party libraries should be reported to the respective project, and also here if they are exploitable through the NMF.
