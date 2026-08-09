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
#   1. Nothing to enable. The Celestia server is on by default, on port 5909,
#      as is the Orekit propagator it needs. Both live in
#          _OPS-SAT-SIMULATOR-header.txt
#      in the directory the Supervisor runs from, which for the playground is
#      under target/ and so is discarded by a rebuild.
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
