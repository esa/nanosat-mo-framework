# Celestia, for looking at the spacecraft the NMF simulator is propagating.
#
# Celestia is not in the Ubuntu archive: it was dropped over its dependency on
# Qt4 and never came back. The 1.7 line is published for Ubuntu through the
# openSUSE Build Service, which is what is used here. The 1.6.4 packages on
# celestiaproject.space stop at 22.04 (jammy) and so are of no use on 24.04.
#
# This image only holds Celestia. The script that would drive it from the
# simulator does not exist yet; see README.md.

# Celestia 1.6.4, not 1.7.
#
# 1.7 is the maintained line and installs cleanly on 24.04, but its packages do
# not act on the ScriptedOrbit and ScriptedRotation entries of a .ssc: the
# object is created and the Lua module it names is never loaded, which was
# established with a spacecraft definition written for the purpose. Those two
# entries are how the simulator drives the spacecraft, so without them there is
# nothing to build on.
#
# 1.6.4 is the version this interface was written against, and it is published
# for Ubuntu only up to jammy. Which Ubuntu the host runs is beside the point
# inside a container, so the image is built on jammy.
FROM ubuntu:22.04

ARG CELESTIA_SUITE=jammy
ARG CELESTIA_REPO=https://celestiaproject.space/ubuntu

RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates curl gnupg \
 && curl -fsSL -o /usr/share/keyrings/celestiaproject.asc \
      https://celestiaproject.space/celestiaproject.key \
 && echo "deb [signed-by=/usr/share/keyrings/celestiaproject.asc] ${CELESTIA_REPO}/ ${CELESTIA_SUITE} main" \
      > /etc/apt/sources.list.d/celestia.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends \
      celestia \
      celestia-common \
      libgl1-mesa-dri \
      fonts-dejavu-core \
      tzdata \
      lua-socket \
      unzip \
 && rm -rf /var/lib/apt/lists/*

# Celestia scripts have no networking of their own, and the interface to the
# simulator is a socket, so LuaSocket is installed above. Celestia embeds
# LuaJIT, which is Lua 5.1, so the 5.1 tree is the one to point at. The trailing
# ";;" keeps the built-in paths as well.
ENV LUA_PATH="/usr/share/lua/5.1/?.lua;/usr/share/lua/5.1/?/init.lua;;"
ENV LUA_CPATH="/usr/lib/x86_64-linux-gnu/lua/5.1/?.so;;"

# Qt talks to the X server through GLX by default, and Celestia then dies on
#   epoxy_get_proc_address: Assertion `0 && "Couldn't find current GLX or EGL
#   context."' failed
# because no context is current by the time it asks for one. Asking Qt for EGL
# instead avoids it. This is not a container problem: hardware rendering works
# either way, and glxinfo reports direct rendering in both.
ENV QT_XCB_GL_INTEGRATION=xcb_egl

# Celestia keeps its settings and bookmarks under the home directory. The
# container is run as the invoking user so that anything it writes to a mounted
# home belongs to that user rather than to root.
ENV HOME=/home/celestia

# The home directory, and the place Celestia keeps its settings and bookmarks
# within it. The container is run as whichever user invoked it, whose id is not
# known when the image is built, so the whole tree is left writable by anyone.
# Without this, closing Celestia fails with "Error Saving Bookmarks: Permission
# denied", because it cannot write into a directory owned by root.
#
# The extras directory is where the add-ons go: the spacecraft definition and
# the script that moves it, once they exist. run.sh mounts it from the host.
RUN mkdir -p ${HOME}/.local/share/celestia/extras \
 && chmod -R 777 ${HOME}

# The startup script and the hook are mounted here rather than into the home
# directory. The home directory is itself a mount, and a file mounted inside
# another mount is shadowed by it: Celestia then reads an empty file that Docker
# left as the mountpoint, and neither the script nor the hook does anything.
RUN mkdir -p /opt/nmf && chmod 755 /opt/nmf

WORKDIR ${HOME}

COPY entrypoint.sh /usr/local/bin/celestia-entrypoint
RUN chmod +x /usr/local/bin/celestia-entrypoint


# A better Earth to look down at.
#
# The packaged textures are small: the day map is a couple of megabytes and the
# night side, which is the one on show when the spacecraft is over the dark half,
# is forty kilobytes. These are NASA's, and are fetched rather than kept in the
# repository, which has no business carrying tens of megabytes of imagery.
#
# Credit: NASA Earth Observatory. Blue Marble: Next Generation for the day side,
# Earth at Night for the night side. NASA material is not subject to copyright;
# NASA asks only to be named as the source, which is what this comment and the
# README do.
#
# Nothing here is allowed to fail quietly. A missing or truncated file would
# leave an image that looks subtly worse with nothing to say why, so every step
# is checked and the build stops if anything is wrong.
#
# Both are within GL_MAX_TEXTURE_SIZE, which is 16384 on the hardware this was
# built for. The 21600 wide Blue Marble is deliberately not used: it cannot be
# loaded as one texture.
ARG NASA_EARTH_DAY=https://eoimages.gsfc.nasa.gov/images/imagerecords/73000/73909/world.topo.bathy.200412.3x5400x2700.jpg
ARG NASA_EARTH_NIGHT=https://eoimages.gsfc.nasa.gov/images/imagerecords/79000/79765/dnb_land_ocean_ice.2012.13500x6750.jpg

RUN set -eu; \
    fetch() { \
        url="$1"; out="$2"; least="$3"; \
        curl --fail --location --silent --show-error --retry 3 --retry-delay 2 \
             --connect-timeout 30 --max-time 600 --output "$out" "$url" \
          || { echo "FAILED to download $url" >&2; exit 1; }; \
        size=$(stat -c%s "$out"); \
        [ "$size" -ge "$least" ] \
          || { echo "FAILED: $out is $size bytes, expected at least $least" >&2; exit 1; }; \
        head -c 2 "$out" | od -An -tx1 | tr -d ' \n' | grep -q '^ffd8' \
          || { echo "FAILED: $out is not a JPEG, the server likely returned an error page" >&2; exit 1; }; \
        echo "fetched $out ($size bytes)"; \
    }; \
    fetch "${NASA_EARTH_DAY}"   /tmp/earth.jpg      2000000; \
    fetch "${NASA_EARTH_NIGHT}" /tmp/earthnight.jpg 5000000; \
    for res in medres hires; do \
        mkdir -p "/usr/share/celestia/textures/$res"; \
        # The definitions ask for "earth.*", so the packaged ones are removed
        # rather than left beside these, which would make the choice arbitrary.
        rm -f "/usr/share/celestia/textures/$res/earth.png" \
              "/usr/share/celestia/textures/$res/earth.jpg" \
              "/usr/share/celestia/textures/$res/earthnight.png" \
              "/usr/share/celestia/textures/$res/earthnight.jpg"; \
        cp /tmp/earth.jpg      "/usr/share/celestia/textures/$res/earth.jpg"; \
        cp /tmp/earthnight.jpg "/usr/share/celestia/textures/$res/earthnight.jpg"; \
    done; \
    rm -f /tmp/earth.jpg /tmp/earthnight.jpg; \
    test -s /usr/share/celestia/textures/medres/earth.jpg; \
    test -s /usr/share/celestia/textures/medres/earthnight.jpg

# Clouds.
#
# The cloud map Celestia ships is small, and clouds are the part of the Earth
# nearest the camera, so it shows. This add-on is John van Vliet's, built from
# NASA's Blue Marble cloud_combined_8192.tif and published on the Celestia
# Motherlode. It is fetched at build time for the same reason as the textures
# above: seventy-eight megabytes has no place in this repository.
#
# Credit: NASA Blue Marble for the data, John van Vliet for the add-on. He
# states no licence terms; the underlying NASA imagery is public domain.
#
# It is a virtual texture, a pyramid of 512 pixel tiles, topping out at
# 8192x4096. That is well inside GL_MAX_TEXTURE_SIZE, so the tiling is not
# needed on modern hardware; it is how the add-on was published in 2010, when
# cards could not hold a texture that large, and there is no reason to unpick
# it.
#
# It carries its own .ssc, which reopens the Earth with "Modify" and sets
# CloudSpeed to 0. That matters: at the shipped speed of 65 a cloud map this
# detailed is dragged into white stripes around the globe. Because the add-on
# does this itself, solarsys.ssc is left alone.
#
# It goes in the image's own extras directory, which Celestia already searches,
# rather than the one mounted from the host, which belongs to the spacecraft.
ARG CLOUD_ADDON=http://www.celestiamotherlode.net/creators/johnvanvliet/VT/EarthClouds_L0123.zip

RUN set -eu; \
    curl --fail --location --silent --show-error --retry 3 --retry-delay 2 \
         --connect-timeout 30 --max-time 900 --output /tmp/clouds.zip "${CLOUD_ADDON}" \
      || { echo "FAILED to download ${CLOUD_ADDON}" >&2; exit 1; }; \
    size=$(stat -c%s /tmp/clouds.zip); \
    [ "$size" -ge 70000000 ] \
      || { echo "FAILED: /tmp/clouds.zip is $size bytes, expected at least 70000000" >&2; exit 1; }; \
    head -c 2 /tmp/clouds.zip | od -An -tx1 | tr -d ' \n' | grep -q '^504b' \
      || { echo "FAILED: /tmp/clouds.zip is not a ZIP, the server likely returned an error page" >&2; exit 1; }; \
    unzip -q /tmp/clouds.zip -d /tmp/clouds \
      || { echo "FAILED to unpack /tmp/clouds.zip" >&2; exit 1; }; \
    test -d /tmp/clouds/extras/JohnVV \
      || { echo "FAILED: the archive does not hold extras/JohnVV as expected" >&2; exit 1; }; \
    cp -a /tmp/clouds/extras/JohnVV /usr/share/celestia/extras/; \
    rm -rf /tmp/clouds /tmp/clouds.zip; \
    test -s /usr/share/celestia/extras/JohnVV/EarthClouds.ssc \
      || { echo "FAILED: EarthClouds.ssc is missing, so the clouds would not load" >&2; exit 1; }; \
    test -s /usr/share/celestia/extras/JohnVV/textures/hires/EarthClouds.ctx \
      || { echo "FAILED: EarthClouds.ctx is missing, so the tiles would not be found" >&2; exit 1; }; \
    tiles=$(find /usr/share/celestia/extras/JohnVV/textures/hires/EarthClouds/level3 \
                 -name 'tx_*.png' | wc -l); \
    [ "$tiles" -eq 128 ] \
      || { echo "FAILED: expected 128 tiles at level3, found $tiles" >&2; exit 1; }; \
    echo "clouds installed: 8192x4096, $tiles tiles at the top level"

# Celestia looks for the modules a .ssc names under "?.lua" and "celxx/?.lua",
# relative to the directory it runs in, which is its own data directory. Nothing
# mounted from the host is on that path, so the module is answered for from both
# of those names here. The file placed there only reads the real one out of the
# mounted add-on directory, which keeps it editable without a rebuild.
COPY module-shim.lua /usr/share/celestia/celxx/orbitattitude-realtime.lua
COPY module-shim.lua /usr/share/celestia/orbitattitude-realtime.lua

ENTRYPOINT ["/usr/local/bin/celestia-entrypoint"]
