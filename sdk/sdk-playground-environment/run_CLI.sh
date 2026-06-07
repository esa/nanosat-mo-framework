#!/bin/sh
#
# Starts the NMF CLI Tool.
# A command-line interface to NMF services (directory lookup, parameter get/set,
# action execution, archive queries).
#
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME="$SCRIPT_DIR/target/space-filesystem/nanosat-mo-framework"
if [ ! -d "$NMF_HOME" ]; then
    echo "Playground environment not built. Run: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
CLI_JAR=$(ls "$SCRIPT_DIR/../cli-tool/target/cli-tool-"*"-jar-with-dependencies.jar" 2>/dev/null | head -1)
if [ -z "$CLI_JAR" ]; then
    echo "CLI jar not found. Build the project first with: mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true"
    exit 1
fi
exec java -Djava.util.logging.config.file="$NMF_HOME/etc/logging.properties" -jar "$CLI_JAR" "$@"
