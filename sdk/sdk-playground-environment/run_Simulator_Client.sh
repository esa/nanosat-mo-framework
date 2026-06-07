#!/bin/sh
#
# Starts the OPS-SAT Spacecraft Simulator Manager.
# The Simulator Manager is a GUI for inspecting and controlling the internal state
# of the software simulator (sensor values, ADCS mode, GPS fix, etc.).
# The NMF Supervisor must be running before opening the Simulator Manager.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME="$SCRIPT_DIR/target/space-filesystem/nanosat-mo-framework"
if [ ! -d "$NMF_HOME" ]; then
    echo "Playground environment not built. Run: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
JARS_NMF=$(ls -d "$NMF_HOME"/jars-nmf/*/ 2>/dev/null | head -1)
JARS_MISSION=$(ls -d "$NMF_HOME"/jars-mission/*/ 2>/dev/null | head -1)
exec java -Djava.util.logging.config.file="$NMF_HOME/etc/logging.properties" -classpath "$JARS_MISSION*:$JARS_NMF*" opssat.simulator.main.MainClient "$@"
