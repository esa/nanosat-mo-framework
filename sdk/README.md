# NanoSat MO Framework - SDK

**Note that most of the following documentation is deprecated. Please refer to the [ReadTheDocs documentation](https://nanosat-mo-framework.readthedocs.io/en/latest/) for up to date information.**

The SDK contains a set of tools and example projects in order to facilitate quicker development of applications by the framework users.

The SDK depends on the NMF Mission - Software Simulator.

## SDK content

### Tools

- [Consumer Test Tool](tools/consumer-test-tool) - allows consuming all of the services exposed by NMF through an user-friendly GUI

### [Space Examples](examples/space)

Various space applications demonstrating the uses of the on-board framework.

### [Ground Examples](examples/ground)

Various ground applications demonstrating the uses of the ground framework.

### [Documentation](../docs/reference-documents)

Various supporting documents, including design documents and development guides.

## Building the SDK Playground Environment

1. Invoke `mvn clean install` from the repository root.
2. The SDK Playground Environment is assembled under `sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/`.

For a faster build: `mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true`

## Running the SDK

1. Start the NMF Supervisor: `sdk-playground-environment/run_Supervisor.sh`
2. Start the Consumer Test Tool: `sdk-playground-environment/run_CTT.sh`
3. Connect the CTT to the Supervisor using the Directory Service URI printed on startup.
4. Run one of the demo apps via the Apps Launcher Service tab.

## Running/Debugging the application from Eclipse IDE

### Importing the projects

1. Go to "File" -> "Import..." -> "Maven" -> "Existing Maven Projects".
2. Browse to and select the root folder of the nanosat-mo-framework repository.
3. Uncheck "Add project(s) to working set".
4. Finish.

All NMF subprojects should be visible inside Eclipse's package explorer.

### Running the Supervisor Simulator and the Consumer Test Tool (CTT)

After a build, the Supervisor Simulator and the CTT are launched from the playground environment using the
convenience scripts in `sdk/sdk-playground-environment/`:

```bash
# Start the Supervisor Simulator (platform services and app lifecycle management)
sdk/sdk-playground-environment/run_Supervisor.sh

# Start the Consumer Test Tool GUI
sdk/sdk-playground-environment/run_CTT.sh
```

In the CTT, connect using the `maltcp://` Directory Service URI printed in the Supervisor log (also written to
`providerURIs.properties` in the Supervisor working directory). The same directory also provides
`run_Simulator_Client.sh` (simulator GUI client) and `run_CLI.sh` (command-line interface).


## Adding an application to the SDK Playground Environment

In order to add your own application to the SDK Playground, follow these steps:

1. Make sure the application is built and installed in your local Maven repository.
2. Open `sdk/sdk-playground-environment/pom.xml` and add your app as an `nmfpack` dependency:

```xml
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>my-app</artifactId>
      <version>1.2.3-SNAPSHOT</version>
      <type>nmfpack</type>
    </dependency>
```

3. Rebuild with `mvn install`. The `nmf-linux-maven-plugin` automatically installs the package into
   `sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/apps/my-app/`.

## Source Code

The source code of the NanoSat MO Framework can be found on [GitHub].

## Bugs Reporting

Bug Reports can be submitted on: [Issues]

## License

The NanoSat MO Framework is **licensed** under the **[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]**.

[![][ESAImage]][website]

[ESAImage]: http://www.esa.int/esalogo/images/logotype/img_colorlogo_darkblue.gif
[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]: https://github.com/esa/nanosat-mo-framework/blob/master/LICENCE.md
[GitHub]: https://github.com/esa
[Issues]: https://gitlab.com/esa/NMF/nmf-issues/-/issues
[website]: http://www.esa.int/
[NanoSat MO Framework]: https://nanosat-mo-framework.github.io/
