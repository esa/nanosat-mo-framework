# Slim runtime image for the NMF lite simulator mission Supervisor.
#
# It consumes the already-generated Space File System, so generate it first:
#   mvn -pl nmf-mission-simulator-lite/lite-space-filesystem package -Pgenerate-filesystem
#   docker build -t nmf-mission-simulator-lite nmf-mission-simulator-lite/lite-space-filesystem
# or do both in one step:
#   mvn -pl nmf-mission-simulator-lite/lite-space-filesystem install -Pdocker
#
# Run (Linux; host networking so a consumer can reach the advertised Directory):
#   docker run --network host nmf-mission-simulator-lite
#
# The orbit of the simulated spacecraft is set through the environment, under the
# same names the Orekit mission takes, so that a constellation can be built out of
# either image:
#   docker run --env KEPLER_A=7021.0 --env KEPLER_E=0.0 --env KEPLER_I=98.05 \
#              --env KEPLER_RAAN=340.0 --env KEPLER_ARG_PER=0.0 --env KEPLER_TRUE_A=0.0 \
#              nmf-mission-simulator-lite
# This mission has no configuration file to write them into: the Supervisor reads
# them itself. Unset elements leave the default orbit of the simulator.

FROM eclipse-temurin:21-jre

# tini as PID 1: reaps the JVM/child processes and forwards signals so that a
# 'docker stop' shuts the Supervisor down cleanly. (Alternatively, drop this and
# run with 'docker run --init'.)
RUN apt-get update \
    && apt-get install -y --no-install-recommends tini \
    && rm -rf /var/lib/apt/lists/*

# Run as a non-root user that owns the NMF home.
RUN useradd --system --create-home --home-dir /home/nmf nmf

# The directory basename must be 'nanosat-mo-framework' so the runtime resolves
# the NMF home from the working directory.
COPY --chown=nmf:nmf target/space-filesystem/nanosat-mo-framework/ /nanosat-mo-framework/

WORKDIR /nanosat-mo-framework
RUN chmod +x start_supervisor.sh

USER nmf

# The Supervisor's maltcp Directory port.
EXPOSE 1024

ENTRYPOINT ["tini", "--", "./start_supervisor.sh"]
