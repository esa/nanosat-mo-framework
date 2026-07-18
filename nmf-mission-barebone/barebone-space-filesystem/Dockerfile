# Slim runtime image for the NMF barebone mission Supervisor.
#
# It consumes the already-generated Space File System, so generate it first:
#   mvn -pl nmf-mission-barebone/barebone-space-filesystem package -Pgenerate-filesystem
#   docker build -t nmf-mission-barebone nmf-mission-barebone/barebone-space-filesystem
# or do both in one step:
#   mvn -pl nmf-mission-barebone/barebone-space-filesystem install -Pdocker
#
# Run (Linux; host networking so a consumer can reach the advertised Directory):
#   docker run --network host nmf-mission-barebone

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
