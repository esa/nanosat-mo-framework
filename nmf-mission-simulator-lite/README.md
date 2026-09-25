## NanoSat MO Framework - Mission - Simulator - Lite

A spacecraft simulator without the orbital mechanics library: the orbit is
worked out analytically, so there is no Orekit and none of the data it carries.

It reports position only. There is no attitude, no magnetic field and no sun
vector, which is the price of leaving the propagator out. If any of those are
needed, use `nmf-mission-simulator-orekit` instead.
