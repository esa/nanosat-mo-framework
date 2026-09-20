-- ---------------------------------------------------------------------------
-- Copyright (C) 2026      European Space Agency
--                         European Space Operations Centre
--                         Darmstadt
--                         Germany
-- ---------------------------------------------------------------------------
-- System                : ESA NanoSat MO Framework
-- ---------------------------------------------------------------------------
-- Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
-- ---------------------------------------------------------------------------
--
-- Drives the spacecraft in Celestia from the NMF simulator.
--
-- opssat.ssc declares a ScriptedOrbit and a ScriptedRotation naming this
-- module, so Celestia asks it where the spacecraft is and which way it is
-- facing every time it draws a frame. Both answers come from the last message
-- the simulator sent.
--
-- The simulator is the server and this is the client, which is the other way
-- round from what the names suggest. It listens on 5909 and speaks a line
-- protocol: it opens with "connection_successful", and every message after that
-- has to be answered with a line before it will send another. A message looks
-- like
--
--   $DATA_START$ $PROTOCOL_VERSION_1.1$ <ids> // <names> // <values> // <units> $DATA_END$
--
-- where the four lists run in parallel, one entry per parameter. The ones used
-- here are X_ICF, Y_ICF and Z_ICF in kilometres, and QS_ICF, QX_ICF, QY_ICF and
-- QZ_ICF for the attitude, scalar part first.
--
-- Nothing blocks. The socket is read without waiting, so a simulator that is
-- not running, or has stopped sending, costs a frame nothing: the spacecraft
-- simply stays where it was last seen.
-- ---------------------------------------------------------------------------

-- Celestia sets its own search paths for the modules a .ssc names, so the ones
-- LuaSocket was installed under are added here rather than left to the
-- environment. Loading it is allowed to fail: if it does, the spacecraft simply
-- never moves, which is better than the definition failing to load at all.
package.path = package.path
    .. ";/usr/share/lua/5.1/?.lua;/usr/share/lua/5.1/?/init.lua"
package.cpath = package.cpath
    .. ";/usr/lib/x86_64-linux-gnu/lua/5.1/?.so"

local loaded, socket = pcall(require, "socket")
if not loaded then
    io.write("orbitattitude-realtime: LuaSocket could not be loaded (",
             tostring(socket), "). The spacecraft will not move.\n")
    socket = nil
end

-- All interfaces, not loopback. Under `docker run --network host` the two are
-- the same thing, but binding loopback would rule out ever running this in a
-- bridge network, where the simulator reaches a published port from outside.
local HOST = "*"
local PORT = 5909

-- How long to wait before trying to take the port again, in seconds of wall
-- clock. Only reached when the bind fails, which in practice means another
-- Celestia already has it.
local RECONNECT_INTERVAL = 3

local ACK = "connection_alive"
local HANDSHAKE = "connection_successful"
local STOP = "connection_stop"

-- The listening socket, opened once and kept for the session. Celestia is the
-- server: it is the long-running end, and a simulator may come and go several
-- times while it runs. Having the simulators dial in is also what lets more
-- than one of them arrive later without Celestia being told where each lives.
local server = {
    socket = nil,
    nextAttempt = 0
}

-- How far behind the simulator the spacecraft is drawn.
--
-- Frames are drawn far more often than positions arrive, so the spacecraft is
-- drawn between two of them rather than at the last one: it then moves every
-- frame instead of standing still and jumping. Drawing between two means
-- having both, which means being one position behind.
--
-- The lag is therefore not a number chosen here but the interval between the
-- last two positions, whatever the simulator is sending at: a tenth of a
-- second of lag where they arrive ten a second, two seconds of it where they
-- arrive every two. Anything less and the moment being drawn runs past the
-- newer of the two, and the spacecraft holds still until the next arrives and
-- then jumps to catch up, which is the very stepping this is here to cure.
--
-- The bounds are there for the odd interval rather than the usual one: a
-- position that arrives late should not pull the picture seconds behind, and
-- two that arrive together should not leave it with no lag at all.
local MIN_LAG = 0.05
local MAX_LAG = 3.0

-- How quickly the estimate of the interval settles back towards a shorter one.
--
-- It is taken at once when an interval is longer than the estimate, and eased
-- towards a shorter one at this much of the difference per position. A late
-- position has to be answered immediately, or the picture runs out of what to
-- draw and stops; a run of prompt ones only means the lag can afford to come
-- down, which is no hurry.
local PACE_RELEASE = 0.05

-- How fast the lag itself is allowed to change, as a fraction of real time.
--
-- The estimate is what the lag is aiming at; this is how quickly it is allowed
-- to get there. Moving it is moving the moment being drawn, and moving that
-- abruptly is a spacecraft that jumps, or worse, steps backwards. At a tenth
-- of real time the picture briefly runs a tenth slow or fast instead, which is
-- not something the eye picks out of an orbit.
local SLEW = 0.1

-- How quickly the reading of the simulator's clock rate settles, as a fraction
-- of the difference per sample. Slow: the rate is a property of the simulator,
-- not of any one sample's delivery, and only changes when someone changes it.
local RATE_SETTLE = 0.02

-- Everything this module remembers between frames.
-- What the spacecraft is doing, shown in the corner of the screen: the mode it
-- is flying, and the moment of the last position to arrive.
--
-- Celestia is asked once a frame and each asking lasts a little longer than a
-- frame, so what is shown stays up rather than blinking.
local SHOW_STATUS = true
local STATUS_SHOWN_FOR = 0.2

-- Which corner, and how far in from it.
--
-- The left-hand one, because Celestia lays the text out rightwards from the
-- point named here: from the left that is simply the text, while from the
-- right it is the text hanging off the edge unless the offset is made to
-- match its width, which changes with what it says.
--
-- The offsets are not pixels. They are of the order of sixteen pixels each,
-- so these are a couple of dozen pixels in from the left and some sixty up
-- from the bottom, which clears Celestia's own line about the speed.
local STATUS_LEFT = -1
local STATUS_BOTTOM = -1
local STATUS_FROM_LEFT = 2
local STATUS_FROM_BOTTOM = 4

local link = {
    socket = nil,
    connected = false,
    -- What is shown in the corner: the attitude mode the simulator reports,
    -- and the moment of the last position as it wrote it.
    mode = nil,
    shownStamp = nil,
    -- The positions and attitudes lately arrived, oldest first, each with the
    -- moment it is of and the moment it arrived. The spacecraft is drawn
    -- between two of them that are both already here.
    buffer = {},
    -- How late delivery has lately been running, in seconds, and how far
    -- behind the newest sample the spacecraft is being drawn. The lag follows
    -- the interval plus that lateness, at the rate SLEW allows.
    jitter = nil,
    rate = nil,
    lag = nil,
    -- The clock the picture is drawn by, and when it was last advanced.
    moment = nil,
    momentAt = nil,

    -- Where the spacecraft is drawn before any position has arrived.
    -- Kept at a plausible altitude so that something is drawn before the first
    -- message arrives, rather than the spacecraft sitting in the centre of the
    -- Earth. Over the equator rather than over a pole, because the startup
    -- script places the observer from wherever the spacecraft is when Celestia
    -- first asks, which is usually here: a spacecraft on the axis is the one
    -- position from which there is no north to put at the top of the image.
    position = {x = 7000, y = 0, z = 0},
    orientation = {w = 1, x = 0, y = 0, z = 0},
    received = 0
}

--- The clock this module measures by, in seconds, or nil where there is none.
--
-- LuaSocket's is used, being the only clock here with a fraction of a second to
-- it. It times the arrival of samples; the picture itself is drawn by
-- Celestia's clock, which is the one the frame belongs to.
local function now()
    if socket ~= nil then
        return socket.gettime()
    end
    return nil
end

-- Seconds in the day that Celestia counts its time in.
local SECONDS_A_DAY = 86400

-- How far apart the samples kept for drawing are, in seconds of the
-- simulator's clock.
--
-- Positions arrive far more often than this, and unevenly: about a third of
-- them a loop late. Keeping one every so often instead of every one makes the
-- intervals even, which is what the picture is made of: the curve between two
-- samples is only as steady as the interval it is spread across.
--
-- The ones passed over are not lost, exactly. The curve is drawn through the
-- ones kept and leaves each at the velocity the simulator gave it, so it goes
-- the way the passed-over ones went. What it costs is half a second of lag,
-- and the curvature the velocities do not capture: a metre or so over half a
-- second, against a spacecraft drawn 210 km across.
local SAMPLE_EVERY = 0.5

-- How many samples are kept, and how far behind the newest of them the picture
-- is drawn, counted in intervals.
--
-- Drawing between the two newest means drawing an interval that is only just
-- complete: the sample that ends it has this instant arrived, and if it
-- arrives late there is nothing to draw until it does. Drawing an interval
-- further back means both of its ends were in hand before it began, and a
-- late sample is merely a sample that arrived while something else was being
-- drawn. What it costs is another interval of lag.
local BEHIND = 2
local KEPT = 6

--- Reads the moment a sample is of, as the simulator writes it.
--
-- The form is ISO 8601, yyyy-mm-ddThh:mm:ss.sssZ, the seconds carrying the
-- milliseconds that tell ten samples of one second apart. What comes back is
-- seconds on this machine's reckoning of that date, which is not the same
-- number the simulator would write, but is out by a fixed amount: only
-- differences are ever taken of it.
--
-- @return The moment in seconds, or nil when there is no reading it.
local function stampOf(text)
    if text == nil then
        return nil
    end

    local year, month, day, hour, minute, second, fraction =
        string.match(text, "(%d+)-(%d+)-(%d+)T(%d+):(%d+):(%d+)%.?(%d*)")

    if year == nil then
        return nil
    end

    local seconds = os.time({
        year = tonumber(year), month = tonumber(month), day = tonumber(day),
        hour = tonumber(hour), min = tonumber(minute), sec = tonumber(second),
        isdst = false
    })

    if seconds == nil then
        return nil
    end

    if fraction ~= nil and fraction ~= "" then
        seconds = seconds + tonumber("0." .. fraction)
    end
    return seconds
end

-- ---------------------------------------------------------------------------
-- The link to the simulator
-- ---------------------------------------------------------------------------

local function disconnect()
    if link.socket ~= nil then
        link.socket:close()
        link.socket = nil
    end
    link.connected = false
end

--- Takes the port, once. Returns whether it is listening.
local function listen()
    if server.socket ~= nil then
        return true
    end
    if os.time() < server.nextAttempt then
        return false
    end

    local sock, err = socket.bind(HOST, PORT)
    if sock == nil then
        -- Almost always another Celestia holding the port. Say so once per
        -- attempt rather than once per frame.
        io.write("orbitattitude-realtime: cannot listen on ", tostring(PORT),
                 " (", tostring(err), "). Retrying.\n")
        server.nextAttempt = os.time() + RECONNECT_INTERVAL
        return false
    end

    -- A frame is being drawn: accept must return at once whether or not
    -- anyone is waiting.
    sock:settimeout(0)
    server.socket = sock
    return true
end

--- Takes a waiting simulator, if there is one, and completes the handshake.
--- Returns whether a link is now up.
local function acceptOne()
    local sock = server.socket:accept()
    if sock == nil then
        -- Nobody waiting, which is the normal case.
        return false
    end

    -- Long enough to complete a handshake on the loopback, short enough that a
    -- simulator which connects and then says nothing does not hold up a frame.
    sock:settimeout(0.25)

    -- The simulator speaks first, as it always did: which end dialled does not
    -- change who greets whom.
    local greeting = sock:receive("*l")
    if greeting == nil or greeting:find(HANDSHAKE, 1, true) == nil then
        sock:close()
        return false
    end

    -- The simulator waits for an answer to the greeting before it sends data.
    sock:send(ACK .. "\n")

    -- From here on nothing is allowed to block: a frame is being drawn.
    sock:settimeout(0)

    link.socket = sock
    link.connected = true
    return true
end

-- ---------------------------------------------------------------------------
-- Reading what the simulator says
-- ---------------------------------------------------------------------------

--- Splits a message into the four lists it is made of, and pairs the names
--- with the values. Returns a table of name to value, or nil if the message is
--- not one.
local function parseMessage(message)
    if message:find("$DATA_START$", 1, true) == nil then
        return nil
    end

    local sections = {}
    for section in (message .. " //"):gmatch("(.-) //") do
        sections[#sections + 1] = section
    end
    -- ids, names, values, units. The units are not used here.
    if #sections < 3 then
        return nil
    end

    local names = {}
    for name in sections[2]:gmatch("%S+") do
        names[#names + 1] = name
    end

    local values = {}
    for value in sections[3]:gmatch("%S+") do
        values[#values + 1] = value
    end

    local parameters = {}
    for i = 1, #names do
        parameters[names[i]] = values[i]
    end
    return parameters
end

--- Takes the position and attitude out of a parsed message, leaving what was
--- there before if the message does not carry them.
---
--- What arrives becomes the newest of the two samples the spacecraft is drawn
--- between, and what was newest becomes the older of them.
local function apply(parameters)
    local x = tonumber(parameters["X_ICF"])
    local y = tonumber(parameters["Y_ICF"])
    local z = tonumber(parameters["Z_ICF"])
    if x ~= nil and y ~= nil and z ~= nil then
        link.position.x = x
        link.position.y = y
        link.position.z = z
    end

    local qs = tonumber(parameters["QS_ICF"])
    local qx = tonumber(parameters["QX_ICF"])
    local qy = tonumber(parameters["QY_ICF"])
    local qz = tonumber(parameters["QZ_ICF"])
    if qs ~= nil and qx ~= nil and qy ~= nil and qz ~= nil then
        link.orientation.w = qs
        link.orientation.x = qx
        link.orientation.y = qy
        link.orientation.z = qz
    end

    -- The velocity is the simulator's own, sent beside the position. It is what
    -- makes the curve between two positions the arc of the orbit rather than
    -- the chord across it.
    local vx = tonumber(parameters["VX_ICF"]) or 0
    local vy = tonumber(parameters["VY_ICF"]) or 0
    local vz = tonumber(parameters["VZ_ICF"]) or 0

    local at = now()
    local stamp = stampOf(parameters["SIM_EPOCH_TIME"]) or at

    -- Kept only when it is far enough past the one before it: an even interval
    -- draws a steadier curve than every sample at an uneven one.
    local newest = link.buffer[#link.buffer]

    if stamp ~= nil and newest ~= nil and stamp - newest.stamp < SAMPLE_EVERY then
        stamp = nil
    end

    if stamp ~= nil then
        local older = newest

        if older ~= nil and at ~= nil and older.at ~= nil then
            -- How long the two samples took to arrive, and how much of the
            -- simulator's own clock they cover.
            local framed = at - older.at
            local covered = stamp - older.stamp

            -- How fast the simulator's clock runs against Celestia's. Ordinarily
            -- one for one, but a simulator can be told to run time faster.
            -- Taken as a long average: a single pair says as much about how late
            -- that one sample was as about the rate, and a rate that followed
            -- the lateness would have the picture speeding up and slowing down
            -- with the delivery.
            if framed > 0 and covered > 0 then
                local r = covered / framed

                if r > 0.001 and r < 1000 then
                    if link.rate == nil then
                        link.rate = r
                    else
                        link.rate = link.rate + (r - link.rate) * RATE_SETTLE
                    end
                end
            end

            -- How late the sample is beyond the interval it covers. The interval
            -- is known exactly now, so what is left to allow for is only the
            -- unevenness of the delivery, and the lag has to cover that or the
            -- picture runs out of positions to draw between.
            --
            -- Taken at once when it grows, and eased down by degrees: a late
            -- sample has to be answered immediately, while a run of prompt ones
            -- only means the lag can afford to come down, which is no hurry.
            local late = framed * (link.rate or 1) - covered

            if late < 0 then
                late = 0
            end

            if link.jitter == nil or late > link.jitter then
                link.jitter = late
            else
                link.jitter = link.jitter + (late - link.jitter) * PACE_RELEASE
            end
        end

        link.buffer[#link.buffer + 1] = {
            at = at,
            stamp = stamp,
            x = link.position.x, y = link.position.y, z = link.position.z,
            vx = vx, vy = vy, vz = vz,
            qw = link.orientation.w, qx = link.orientation.x,
            qy = link.orientation.y, qz = link.orientation.z
        }

        -- Only the last few are of any use: the picture is drawn a couple of
        -- intervals behind, and what is older than that is never asked for
        -- again.
        while #link.buffer > KEPT do
            table.remove(link.buffer, 1)
        end
    end

    -- What the simulator says of itself: the propagator and the attitude mode,
    -- as "Time|x1|Kepler|NADIR_POINTING". The mode is the last of those.
    local info = parameters["INFO"]

    if info ~= nil then
        local mode = string.match(info, "([^|]+)$")

        if mode ~= nil then
            link.mode = mode
        end
    end

    -- The moment as the simulator wrote it, kept for the corner of the screen.
    -- The drawing uses the moment read out of it, a number of seconds; this is
    -- the text of it.
    link.shownStamp = parameters["SIM_EPOCH_TIME"] or link.shownStamp

    link.received = link.received + 1
end

-- ---------------------------------------------------------------------------
-- Drawing between two positions
-- ---------------------------------------------------------------------------

--- How far between the two samples the moment being drawn falls.
---
--- @return The fraction, from 0 at the older sample to 1 at the newer, and the
--- seconds between the two; or nil when there are not two to draw between.
local function between()
    local buffer = link.buffer
    local newest = buffer[#buffer]

    if newest == nil or #buffer < 2 then
        return nil
    end

    local span = newest.stamp - buffer[#buffer - 1].stamp

    if span <= 0 then
        return nil
    end

    local rate = link.rate or 1
    local wall = now()

    if wall == nil then
        return nil
    end

    -- Behind the newest sample by a couple of intervals, plus however late
    -- delivery has lately been running. Two intervals is what makes the one
    -- being drawn an interval whose ends both arrived before it began.
    link.lag = BEHIND * span + (link.jitter or 0)

    if link.lag < MIN_LAG then
        link.lag = MIN_LAG
    elseif link.lag > MAX_LAG then
        link.lag = MAX_LAG
    end

    -- The moment being drawn runs on a clock of its own: it advances with this
    -- machine's, and is steered towards where the samples say it should be
    -- rather than being set from them. Setting it from them steps it back
    -- whenever a late sample arrives, and a step back is a spacecraft that
    -- twitches; a clock that is only nudged runs a little slow or fast for a
    -- moment instead.
    local target = newest.stamp + (wall - newest.at) * rate - link.lag

    if link.moment == nil then
        link.moment = target
    else
        local elapsed = (wall - link.momentAt) * rate

        link.moment = link.moment + elapsed

        local drift = target - link.moment
        local most = elapsed * SLEW

        if drift > most then
            drift = most
        elseif drift < -most then
            drift = -most
        end

        link.moment = link.moment + drift
    end

    link.momentAt = wall

    -- The two samples the moment falls between. Both are already here: that is
    -- the whole point of drawing this far back.
    local older, newer = buffer[1], buffer[2]

    for i = 1, #buffer - 1 do
        if buffer[i].stamp <= link.moment and link.moment <= buffer[i + 1].stamp then
            older, newer = buffer[i], buffer[i + 1]
            break
        elseif link.moment > buffer[i + 1].stamp then
            older, newer = buffer[i], buffer[i + 1]
        end
    end

    local reach = newer.stamp - older.stamp

    if reach <= 0 then
        return nil
    end

    local fraction = (link.moment - older.stamp) / reach

    -- Before the oldest sample, and past the newest when the next is late: the
    -- spacecraft is held at the end it has rather than carried beyond it.
    if fraction < 0 then
        fraction = 0
    elseif fraction > 1 then
        fraction = 1
    end
    return fraction, reach, older, newer
end

--- Where the spacecraft is at the moment being drawn.
---
--- The curve between two samples is the one that passes through both and
--- leaves each at the velocity the simulator gave it, so it follows the arc of
--- the orbit rather than cutting across it, and the speed does not jump as one
--- sample gives way to the next.
---
--- @return Three numbers, in km, in the frame the .ssc declares.
local function positionNow()
    local fraction, span, older, newer = between()

    if fraction == nil then
        return link.position.x, link.position.y, link.position.z
    end

    local s = fraction
    local s2 = s * s
    local s3 = s2 * s

    -- The Hermite weights: the two positions, and the two velocities over the
    -- span they are being spread across.
    local wOlder = 2 * s3 - 3 * s2 + 1
    local wNewer = -2 * s3 + 3 * s2
    local wOlderV = (s3 - 2 * s2 + s) * span
    local wNewerV = (s3 - s2) * span

    return older.x * wOlder + newer.x * wNewer + older.vx * wOlderV + newer.vx * wNewerV,
           older.y * wOlder + newer.y * wNewer + older.vy * wOlderV + newer.vy * wNewerV,
           older.z * wOlder + newer.z * wNewer + older.vz * wOlderV + newer.vz * wNewerV
end

--- The attitude at the moment being drawn, turned the shortest way between the
--- two samples.
---
--- Nothing is sent about how fast the spacecraft is turning, so there is no
--- curve to follow here as there is for the position: the attitude is simply
--- taken the short way round from one sample to the next.
---
--- @return A table of w, x, y, z, as the rest of this module passes attitudes.
local function orientationNow()
    local fraction, _, older, newer = between()

    if fraction == nil then
        return link.orientation
    end

    local dot = older.qw * newer.qw + older.qx * newer.qx
              + older.qy * newer.qy + older.qz * newer.qz

    -- A quaternion and its negative are the same attitude, and the two samples
    -- may be written either way round. Taking the nearer of the two keeps the
    -- spacecraft from turning the long way about when the sign flips.
    local sign = 1

    if dot < 0 then
        dot = -dot
        sign = -1
    end

    local a, b

    if dot > 0.9995 then
        -- Barely any turn between them: the straight blend is within the
        -- rounding of the arc, and has no small angle to divide by.
        a = 1 - fraction
        b = fraction * sign
    else
        local angle = math.acos(dot)
        local sine = math.sin(angle)
        a = math.sin((1 - fraction) * angle) / sine
        b = math.sin(fraction * angle) / sine * sign
    end

    local w = older.qw * a + newer.qw * b
    local x = older.qx * a + newer.qx * b
    local y = older.qy * a + newer.qy * b
    local z = older.qz * a + newer.qz * b
    local norm = math.sqrt(w * w + x * x + y * y + z * z)

    if norm == 0 then
        return link.orientation
    end
    return {w = w / norm, x = x / norm, y = y / norm, z = z / norm}
end

--- Takes whatever the simulator has sent since the last frame, answers each
--- message, and keeps the newest. Called once per frame and never waits.
local function poll()
    -- What instant this frame is for. Everything the picture is timed by is
    -- measured against it.
    if socket == nil then
        return
    end

    if not listen() then
        return
    end

    if not link.connected then
        if not acceptOne() then
            return
        end
    end

    while true do
        local line, err = link.socket:receive("*l")

        if line == nil then
            if err == "timeout" then
                -- Nothing more to read this frame, which is the normal case.
                return
            end
            -- The other end has gone away.
            disconnect()
            return
        end

        if line:find(STOP, 1, true) ~= nil then
            disconnect()
            return
        end

        local parameters = parseMessage(line)
        if parameters ~= nil then
            apply(parameters)
            -- Every message has to be answered or the simulator resends it and
            -- then gives up.
            if link.socket:send(ACK .. "\n") == nil then
                disconnect()
                return
            end
        end
    end
end

-- ---------------------------------------------------------------------------
-- Attitude frames
-- ---------------------------------------------------------------------------

-- Celestia does not want the attitude in the frame the simulator reports it in.
-- It has to be turned a quarter turn about the first axis, and expressed in the
-- axes Celestia uses internally, which are not those of the definition. The two
-- together are the mapping below.
--
-- It is not a plain multiplication by a fixed quaternion, which is why it is
-- written out term by term rather than composed.
local HALF_ROOT_TWO = math.sqrt(2) / 2

--- Turns an attitude as the simulator reports it into one Celestia will accept,
--- returning four numbers with the scalar part first.
local function toCelestiaFrame(q)
    local w = -q.w * HALF_ROOT_TWO - q.x * HALF_ROOT_TWO
    local x = -q.w * HALF_ROOT_TWO + q.x * HALF_ROOT_TWO
    local y =  q.z * HALF_ROOT_TWO + q.y * HALF_ROOT_TWO
    local z =  q.z * HALF_ROOT_TWO - q.y * HALF_ROOT_TWO

    local norm = math.sqrt(w * w + x * x + y * y + z * z)
    if norm == 0 then
        return 1, 0, 0, 0
    end
    return w / norm, x / norm, y / norm, z / norm
end

-- ---------------------------------------------------------------------------
-- What Celestia asks for
-- ---------------------------------------------------------------------------

--- Shows what the spacecraft is doing, in the corner of the screen.
---
--- A commanded turn is otherwise only to be recognised by the spacecraft
--- beginning to move, which is some seconds after the mode has changed.
local function showStatus()
    if not SHOW_STATUS or celestia == nil then
        return
    end

    local text = link.mode or ""

    if link.shownStamp ~= nil then
        -- The wire keeps what ISO 8601 asks for: a T between the date and the
        -- time, and a Z for the zone. Read off a screen, a space is easier on
        -- the eye, and there is only one clock here for the Z to distinguish
        -- it from.
        local when = string.gsub(link.shownStamp, "T", " ")
        when = string.gsub(when, "Z$", "")
        text = text .. "\n" .. when
    end

    if text == "" then
        return
    end

    pcall(function()
        celestia:print(text, STATUS_SHOWN_FOR, STATUS_LEFT, STATUS_BOTTOM,
                       STATUS_FROM_LEFT, STATUS_FROM_BOTTOM)
    end)
end

--- Named by the ScriptedOrbit in opssat.ssc.
function RealTimeOrbit(parameters)
    local orbit = {}

    -- Far enough out to cover any orbit the simulator is likely to propagate.
    -- Celestia uses this to decide when the object is worth drawing, not where
    -- it is.
    orbit.boundingRadius = 50000

    -- Named "position", not "positionAtTime". Celestia looks for this name and
    -- says nothing at all when it is not there: the orbit is built, never
    -- asked for anything, and the spacecraft sits at the centre of the Earth.
    --
    -- The simulator sends kilometres in the frame the .ssc declares, which is
    -- what Celestia wants here, so the numbers are passed straight through.
    function orbit:position(tjd)
        poll()
        showStatus()
        return positionNow()
    end

    return orbit
end

--- Named by the ScriptedRotation in opssat.ssc.
-- luacheck: globals RealTimeRotation
function RealTimeRotation(parameters)
    local rotation = {}

    -- The attitude comes from outside and does not repeat, so Celestia is told
    -- not to treat it as periodic.
    rotation.period = 0

    -- Named "orientation" for the same reason "position" is named as it is.
    --
    -- Unlike the position, the attitude is not simply passed on. The simulator
    -- reports it against the inertial frame, and Celestia expects it against
    -- the body frame the .ssc declares, which is a quarter turn away. The two
    -- are composed here.
    --
    function rotation:orientation(tjd)
        poll()
        return toCelestiaFrame(orientationNow())
    end

    return rotation
end

-- Celestia asks for this file as a module and then looks for the named
-- functions in what it hands back, so they are returned as well as being left
-- global. Returning nothing leaves Celestia with no functions to call, and it
-- drops the object rather than drawing it in the wrong place.
return {
    RealTimeOrbit = RealTimeOrbit,
    RealTimeRotation = RealTimeRotation
}
