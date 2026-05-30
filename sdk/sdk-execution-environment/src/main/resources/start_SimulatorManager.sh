#!/bin/sh
#
# Starts the OPS-SAT Spacecraft Simulator Manager.
# The Simulator Manager is a GUI for inspecting and controlling the internal state
# of the software simulator (sensor values, ADCS mode, GPS fix, etc.).
# The NMF Supervisor must be running before opening the Simulator Manager.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$SCRIPT_DIR/home/nmf/spacecraft-simulator-gui" || exit 1
exec ./spacecraft-simulator-gui.sh "$@"
