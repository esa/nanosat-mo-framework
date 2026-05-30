#!/bin/sh
#
# Starts the NMF Consumer Test Tool (CTT).
# The CTT is a ground GUI for monitoring and controlling NMF Apps via MO services.
# The NMF Supervisor must be running before connecting the CTT.
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME="$SCRIPT_DIR/target/space-filesystem/nanosat-mo-framework"
if [ ! -d "$NMF_HOME" ]; then
    echo "Playground environment not built. Run: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
CTT_DIR="$SCRIPT_DIR/../consumer-test-tool"
CTT_JAR=$(ls "$CTT_DIR/target/consumer-test-tool-"*"-jar-with-dependencies.jar" 2>/dev/null | head -1)
if [ -z "$CTT_JAR" ]; then
    echo "CTT jar not found. Build the project first with: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
cd "$CTT_DIR" || exit 1
exec java -jar "$CTT_JAR" "$@"
