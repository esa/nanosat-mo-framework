## NanoSat MO Framework - Mission - Simulator - Lite

A spacecraft simulator without the orbital mechanics library: the orbit is
worked out analytically, so there is no Orekit and none of the data it carries.

It reports position only. There is no attitude, no magnetic field and no sun
vector, which is the price of leaving the propagator out. If any of those are
needed, use `nmf-mission-simulator-orekit` instead.

### Showing it in Celestia

The spacecraft can be flown in Celestia, the same viewer the Orekit mission
uses.
Start Celestia with `celestia/run.sh` and give the
lite Supervisor these in its environment:

```
CELESTIA=true          # off by default
CELESTIA_HOST=127.0.0.1  # where Celestia is listening; the loopback by default
CELESTIA_PORT=5909       # its usual port by default
SPACECRAFT_NAME=cubesat  # the name the spacecraft shows under in Celestia
```

Only the position and the velocity are sent, so the spacecraft follows its orbit
with a fixed orientation: the lite simulator has no attitude to show.
The motion is in real time, so at a low orbit the spacecraft moves slowly.
