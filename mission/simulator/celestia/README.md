## NanoSat MO Framework - Mission - Simulator - Celestia

Shows the spacecraft that the Supervisor Simulator is flying, in [Celestia].

The simulator already knew how to feed a visualisation; what was missing was
anything to feed. This directory is the other half: the spacecraft and ground
stations as Celestia objects, the script that drives them from the simulator,
and a container so that nothing has to be installed on the machine.

### Running it

Three steps, from `sdk/sdk-playground-environment`:

1. **Turn the server on in the simulator.** It writes its configuration on first
   run, so start the Supervisor once and then edit what it leaves at
   `_OPS-SAT-SIMULATOR-header.txt`, in the directory the Supervisor runs from,
   which for the playground is
   `sdk/sdk-playground-environment/target/space-filesystem/nanosat-mo-framework/`:

   ```
   orekit=true
   celestia=true
   celestiaPort=5909
   ```

   `orekit=true` is not optional. The samples exist only while the Orekit
   propagator is running, so with Orekit off the server accepts a connection and
   then has nothing to say.

2. **Start the Supervisor Simulator**: `./run_Supervisor.sh`

3. **Start Celestia**: `./run_Celestia.sh`

The simulator is the server and Celestia the client, so Celestia can be started,
stopped and restarted as often as needed while the simulator keeps running. The
first run builds the container image, which takes a few minutes.

`run_Celestia.sh` calls `run.sh` in this directory, which can also be used
directly:

```
./run.sh                # build if needed, then run
./run.sh --fullscreen   # take over the whole screen
./run.sh --rebuild      # build again first
```

Celestia 1.6 takes no option for the window size, so where the window lands is
left to the desktop. Anything else given to the script is passed on to Celestia.

### How it works

```
SimulatorNode ──samples──▶ queue ──▶ CelestiaIf (server, port 5909)
                                            ▲
                                            │ TCP, one client, line protocol
                                            │
                              orbitattitude-realtime.lua inside Celestia
                                  (asked for a position every frame)
```

The direction is the opposite of what the names suggest: `CelestiaIf` in the
simulator is the **server**, and the script inside Celestia is the **client**.

Each message is a line and has to be answered before the next one is sent:

```
$DATA_START$ $PROTOCOL_VERSION_1.1$ <ids> // <names> // <values> // <units> $DATA_END$
```

The four lists run in parallel, one entry per parameter. This side reads `X_ICF`,
`Y_ICF` and `Z_ICF` in kilometres and the four `Q*_ICF` attitude components, and
ignores the rest. Propagation is too slow to answer on demand, so the simulator
generates ahead into a queue; the script takes the newest sample and drops the
others, which keeps the picture current instead of replaying a backlog.

Nothing blocks: the socket is read without waiting, so a simulator that is not
running costs a frame nothing and the spacecraft simply stays where it was.

### What is in this directory

| File | What it does |
|---|---|
| `Dockerfile` | Celestia 1.6.4 and LuaSocket, on jammy |
| `run.sh` | Builds if needed, then runs it with the display, the GPU and the host network |
| `entrypoint.sh` | Derives a Celestia configuration at startup from the one the package ships |
| `module-shim.lua` | Answers for the module name at the only paths Celestia searches |
| `luahook.lua` | Puts the add-on directory on the module search path |
| `startup.cel` | Selects the spacecraft, so that it is drawn |
| `extras/` | The spacecraft, the ground stations and the models they draw with |
| `extras/opssat/celxx/orbitattitude-realtime.lua` | The client: connects, acknowledges, and answers Celestia's questions |
| `tools/stub-simulator.py` | Stands in for the simulator, for working on this without one |

### Which Celestia, and why it matters

**Celestia 1.6.4, not 1.7.** This is not a preference. The packaged 1.7 does not
act on the `ScriptedOrbit` and `ScriptedRotation` entries of a spacecraft
definition: the object is created and the Lua module it names is never loaded.
That was established with a definition written for the purpose, which 1.6.4 loads
and calls and 1.7 ignores. Those entries are the whole mechanism.

Celestia is not in the Ubuntu archive; it was dropped over its dependency on Qt4.
1.6.4 is published for Ubuntu up to jammy at `celestiaproject.space`. Which
Ubuntu this machine runs does not matter, because Celestia runs in a container,
and the image is built on jammy to get those packages.

### Things that cost a day, written down so they do not again

**The methods are called `position` and `orientation`.** Not `positionAtTime` and
`orientationAtTime`. Celestia builds the orbit, finds no method it recognises,
and then silently never asks for anything: no error, no log line, and a
spacecraft sitting at the centre of the Earth. This one symptom looks exactly
like the module never having been loaded.

**Celestia searches `?.lua` and `celxx/?.lua`, relative to its own data
directory**, which is where it runs from. Nothing mounted from the host is on
that path, which is why `module-shim.lua` is placed there when the image is
built; it only reads the real module out of the mounted add-on directory, so that
one stays editable without a rebuild.

**Add-ons load from `--extrasdir`,** not from the `ExtrasDirectories` line of the
generated configuration. Without the option the definition is not loaded at all
and nothing else can work.

**A `wait` in the startup script stops Celestia loading anything.** `select`,
`goto` and `follow` are fine. The symptom is an empty universe, which looks like
a broken installation rather than a broken script.

**Celestia says nothing when any of this goes wrong.** Its log stays empty. The
only way to see where a failure is is to have the Lua write a file, and to open
that file once at load and keep the handle: opening one per call does not survive
whatever Celestia does at render time.

### Working on this without a simulator

`tools/stub-simulator.py` speaks the same protocol and flies a circle:

```
./tools/stub-simulator.py --interval 0.3
```

Then start Celestia as usual. It prints each connection and counts the messages
answered, which is the quickest way to see whether the Celestia side is alive.

### Still to check

The spacecraft follows its orbit correctly, and that has been confirmed against
the stub. The attitude uses the frame mapping of the interface this descends
from, so it should be right, but it has not been watched against a simulator
actually controlling the attitude. If the spacecraft flies at a fixed odd angle,
`toCelestiaFrame` in `orbitattitude-realtime.lua` is what to look at.

The ground station definitions ask for `x-vector.3ds`, `y-vector.3ds` and
`z-vector.3ds`, which are not here, so Celestia reports three missing meshes and
draws the rest. `darmstadt.ssc` also uses Kiruna's visibility cone rather than
one of its own: the station belongs there, since OPS-SAT was flown from the
SMILE lab at ESOC, but the cone drawn for it is not the one it would see.

### Source Code

The source code of the NanoSat MO Framework can be found on [GitHub].

### Bugs Reporting

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
[Celestia]: https://celestiaproject.space/
