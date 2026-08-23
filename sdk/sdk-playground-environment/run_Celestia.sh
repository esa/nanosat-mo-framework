#!/bin/sh
#
# Starts Celestia and shows the spacecraft the Supervisor Simulator is flying.
#
# Celestia runs in a container, so nothing has to be installed on this machine.
# The image is built the first time this is run, which takes a few minutes and
# downloads about a gigabyte; after that it starts straight away.
#
# Order of events:
#
#   1. Nothing to enable. The Celestia link is on by default, to port 5909 on
#      this machine, as is the Orekit propagator it needs. Both live in
#          _SIMULATOR-header.txt
#      in the directory the Supervisor runs from, which for the playground is
#      under target/ and so is discarded by a rebuild.
#
#   2. Start the Supervisor Simulator, from this directory:
#          ./run_Supervisor.sh
#
#   3. Run this script. Celestia takes the port, the simulator dials in, and
#      Celestia opens looking at the spacecraft.
#
# Celestia is the server and the simulator the client, so either can be started
# and stopped while the other keeps running: a simulator that finds nobody
# listening retries every three seconds.
#
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
CELESTIA_DIR=$(cd "$HERE/../../nmf-mission-simulator-orekit/celestia" 2>/dev/null && pwd || true)
PORT=${CELESTIA_PORT:-5909}

if [ -z "$CELESTIA_DIR" ] || [ ! -x "$CELESTIA_DIR/run.sh" ]; then
    echo "Could not find nmf-mission-simulator-orekit/celestia/run.sh from $HERE." >&2
    echo "This script expects to be run from inside the NMF source tree." >&2
    exit 1
fi

if ! command -v docker > /dev/null 2>&1; then
    echo "Docker is not installed, and Celestia runs in a container so that it" >&2
    echo "does not have to be installed on this machine." >&2
    echo "See nmf-mission-simulator-orekit/celestia/README.md." >&2
    exit 1
fi

# Celestia is the one that listens now, so something already on the port is the
# thing worth reporting: another Celestia, or a tap-simulator.py left running.
# Not fatal, because Celestia retries and will take the port when it frees up.
if command -v ss > /dev/null 2>&1; then
    if ss -ltn 2>/dev/null | grep -q ":${PORT}[[:space:]]"; then
        echo "Something is already listening on port ${PORT}. Celestia needs it to"
        echo "accept the simulator, and will keep retrying until it is free."
        echo "See the notes at the top of this script."
        echo
    fi
fi

exec "$CELESTIA_DIR/run.sh" "$@"
