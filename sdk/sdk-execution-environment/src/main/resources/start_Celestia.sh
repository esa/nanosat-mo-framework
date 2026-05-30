#!/bin/sh
#
# Starts Celestia for 3D spacecraft visualisation.
# Celestia connects to the NMF Supervisor Simulator over TCP and displays the
# simulated spacecraft position and attitude in real time.
#
# Prerequisites:
#   1. Celestia must be installed (sudo apt-get install celestia).
#   2. The NMF Supervisor Simulator must be running with Celestia enabled:
#      set  celestia=true  in home/nmf/nanosat-mo-supervisor-sim/simulator.properties
#      (default port: 5909).
#
if ! command -v celestia > /dev/null 2>&1; then
    echo "Celestia not found. Install it with: sudo apt-get install celestia"
    exit 1
fi
exec celestia "$@"
