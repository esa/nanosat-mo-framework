#!/bin/sh
#
# Starts the NMF Supervisor Simulator.
# The Supervisor provides the on-board software stack with simulated platform services
# and manages the lifecycle of NMF Apps.
# Run this script first, before starting the CTT or any NMF Apps.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME="$SCRIPT_DIR/target/space-filesystem/nanosat-mo-framework"
if [ ! -d "$NMF_HOME" ]; then
    echo "Playground environment not built. Run: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
cd "$NMF_HOME" || exit 1
exec ./start_supervisor.sh "$@"
