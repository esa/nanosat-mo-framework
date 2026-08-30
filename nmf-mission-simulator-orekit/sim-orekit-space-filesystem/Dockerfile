# Slim runtime image for the NMF Orekit simulator mission Supervisor.
#
# It consumes the already-generated Space File System, so generate it first:
#   mvn -pl nmf-mission-simulator-orekit/sim-orekit-space-filesystem package -Pgenerate-filesystem
#   docker build -t nmf-mission-simulator-orekit nmf-mission-simulator-orekit/sim-orekit-space-filesystem
# or do both in one step:
#   mvn -pl nmf-mission-simulator-orekit/sim-orekit-space-filesystem install -Pdocker
#
# Run (Linux; host networking so a consumer can reach the advertised Directory):
#   docker run --network host nmf-mission-simulator-orekit
#
# The orbit of the simulated spacecraft is set through the environment, so that a
# constellation of containers is a constellation of orbits:
#   docker run --env KEPLER_A=7021.0 --env KEPLER_E=0.0 --env KEPLER_I=98.05 \
#              --env KEPLER_RAAN=340.0 --env KEPLER_ARG_PER=0.0 --env KEPLER_TRUE_A=0.0 \
#              nmf-mission-simulator-orekit
# Unset elements default to 0.0, which is the orbit the header file carries.

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
RUN chmod +x start_supervisor.sh configure_orbital_dynamics.sh

USER nmf

# The Supervisor's maltcp Directory port.
EXPOSE 1024

# The orbit is written into the simulator configuration before the Supervisor is
# started, and the Supervisor replaces the shell so that it keeps PID 1's signals.
ENTRYPOINT ["tini", "--", "/bin/sh", "-c", "./configure_orbital_dynamics.sh && exec ./start_supervisor.sh"]
