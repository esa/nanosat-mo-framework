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
#   1. Enable the Celestia server in the simulator. It writes its configuration
#      on first run, so start the Supervisor once and then edit
#          target/space-filesystem/nanosat-mo-framework/_OPS-SAT-SIMULATOR-header.txt
#      which is that file in the directory the Supervisor runs from
#      setting
#          orekit=true
#          celestia=true
#          celestiaPort=5909
#      Orekit is not optional: the visualisation data is only produced while its
#      propagator is running.
#
#   2. Start the Supervisor Simulator, from this directory:
#          ./run_Supervisor.sh
#
#   3. Run this script. Celestia connects to the simulator, and opens looking at
#      the spacecraft.
#
# The simulator is the server and Celestia the client, so this can be started
# and stopped as often as needed while the simulator keeps running.
#
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
CELESTIA_DIR=$(cd "$HERE/../../mission/simulator/celestia" 2>/dev/null && pwd || true)
PORT=${CELESTIA_PORT:-5909}

if [ -z "$CELESTIA_DIR" ] || [ ! -x "$CELESTIA_DIR/run.sh" ]; then
    echo "Could not find mission/simulator/celestia/run.sh from $HERE." >&2
    echo "This script expects to be run from inside the NMF source tree." >&2
    exit 1
fi

if ! command -v docker > /dev/null 2>&1; then
    echo "Docker is not installed, and Celestia runs in a container so that it" >&2
    echo "does not have to be installed on this machine." >&2
    echo "See mission/simulator/celestia/README.md." >&2
    exit 1
fi

# Not fatal: Celestia waits for the simulator and connects whenever it appears,
# so it is only worth pointing out.
if command -v ss > /dev/null 2>&1; then
    if ! ss -ltn 2>/dev/null | grep -q ":${PORT}[[:space:]]"; then
        echo "Nothing is listening on port ${PORT}, so the simulator's Celestia"
        echo "server does not appear to be running. Celestia will start anyway and"
        echo "connect when it does. See the notes at the top of this script."
        echo
    fi
fi

exec "$CELESTIA_DIR/run.sh" "$@"
