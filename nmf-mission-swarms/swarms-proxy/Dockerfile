# The Directory service for a whole constellation.
#
# It gives a consumer a single address to find every spacecraft, instead of one
# address per spacecraft. It is a server, so it runs as one: in its own
# container, alongside the segments it serves, with its own logs.
#
# It needs the jar built by this module, so build that first:
#   mvn -pl nmf-mission-swarms/swarms-proxy install
#   docker build -t nmf-constellation-directory nmf-mission-swarms/swarms-proxy
# or do both in one step:
#   mvn -pl nmf-mission-swarms/swarms-proxy install -Pdocker
#
# Run it on the same network as the constellation, and pass it the Directory
# service address of every segment:
#   docker run --network nmf-constellation --ip 172.28.255.253 \
#              nmf-constellation-directory \
#              maltcp://172.28.0.1:1024/nanosat-mo-supervisor-Directory \
#              maltcp://172.28.0.2:1024/nanosat-mo-supervisor-Directory
#
# The Constellation Management Tool does this itself when it raises a
# constellation, and prints the address to connect to.

FROM eclipse-temurin:21-jre

# tini as PID 1: it reaps the JVM and forwards signals, so that a 'docker stop'
# shuts the Directory service down cleanly instead of killing it.
RUN apt-get update \
    && apt-get install -y --no-install-recommends tini \
    && rm -rf /var/lib/apt/lists/*

# Run as an unprivileged user that owns the directory it works in. The service
# keeps no state on disk: its COM Archive is held in memory, because it rebuilds
# what it knows from the segments every time it starts.
RUN useradd --system --create-home --home-dir /home/nmf nmf

WORKDIR /home/nmf

COPY --chown=nmf:nmf target/swarms-proxy-jar-with-dependencies.jar constellation-directory.jar
COPY --chown=nmf:nmf provider.properties transport.properties ./

USER nmf

# The Directory service consumers connect to.
EXPOSE 1024

# The segment addresses are passed as arguments, so they follow the image name
# on the command line.
ENTRYPOINT ["tini", "--", "java", "-jar", "constellation-directory.jar"]
