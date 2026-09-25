#!/bin/sh
#
# Tails the most recent SimulatorNode log from the CubeSat Simulator.
# The Supervisor must be running (or have been run) to produce log files.
#
tail -f -n 5000 "$(ls -1tr ~/.nmf-simulator/Simulator*log 2>/dev/null | tail -1)"
