#!/bin/sh
#
# Starts the NMF Supervisor Simulator.
# The Supervisor provides the on-board software stack with simulated platform services
# and manages the lifecycle of NMF Apps.
# Run this script first, before starting the CTT or any NMF Apps.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$SCRIPT_DIR/home/nmf/nanosat-mo-supervisor-sim" || exit 1
exec ./nanosat-mo-supervisor-sim.sh "$@"
