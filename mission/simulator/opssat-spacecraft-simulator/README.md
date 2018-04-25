OPS-SAT Spacecraft Simulator
=================

It simulates the following spacecraft peripherals:
* Camera
* FineADCS
* GPS
* Optical RX
* SDR

The orbital position for GPS and ADCS is either calculated analitically (using GPS Simulator package), or propagated using Orekit, depending on the `orekit` config variable.

Full project documentation can be found in NMF SDK package.