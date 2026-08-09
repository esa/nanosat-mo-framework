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

local HOST = "127.0.0.1"
local PORT = 5909

-- How long to wait before trying the simulator again, in seconds of wall clock.
local RECONNECT_INTERVAL = 3

local ACK = "connection_alive"
local HANDSHAKE = "connection_successful"
local STOP = "connection_stop"

-- Everything this module remembers between frames.
local link = {
    socket = nil,
    connected = false,
    nextAttempt = 0,
    -- Where the spacecraft was last seen, in the frame the .ssc declares.
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

-- ---------------------------------------------------------------------------
-- The link to the simulator
-- ---------------------------------------------------------------------------

local function disconnect()
    if link.socket ~= nil then
        link.socket:close()
        link.socket = nil
    end
    link.connected = false
    link.nextAttempt = os.time() + RECONNECT_INTERVAL
end

--- Opens the connection and reads the greeting. Returns whether it worked.
local function connect()
    local sock, err = socket.tcp()
    if sock == nil then
        return false
    end

    -- Long enough to complete a handshake on the loopback, short enough that a
    -- simulator which is not there does not hold up a frame.
    sock:settimeout(0.25)

    local ok = sock:connect(HOST, PORT)
    if ok == nil then
        sock:close()
        link.nextAttempt = os.time() + RECONNECT_INTERVAL
        return false
    end

    local greeting = sock:receive("*l")
    if greeting == nil or greeting:find(HANDSHAKE, 1, true) == nil then
        sock:close()
        link.nextAttempt = os.time() + RECONNECT_INTERVAL
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

    link.received = link.received + 1
end

--- Takes whatever the simulator has sent since the last frame, answers each
--- message, and keeps the newest. Called once per frame and never waits.
local function poll()
    if socket == nil then
        return
    end

    if not link.connected then
        if os.time() >= link.nextAttempt then
            connect()
        end
        return
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
        return link.position.x, link.position.y, link.position.z
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
        return toCelestiaFrame(link.orientation)
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
