#!/bin/sh
#
# Tails the most recent CentralNode log from the CubeSat Simulator.
# The Supervisor must be running (or have been run) to produce log files.
#
tail -f "$(ls -1tr ~/.ops-sat-simulator/Cen*log 2>/dev/null | tail -1)"
