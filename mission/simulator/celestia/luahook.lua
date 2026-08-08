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
-- Loaded by Celestia through the LuaHook entry of its configuration, which
-- names a file rather than a module, and so does not depend on where Celestia
-- looks for modules.
--
-- That is the point of it. The spacecraft definition asks for a module by name,
-- and Celestia was not finding it in the add-on directory, so the directory is
-- added to the search path here, before anything asks for the module.
-- ---------------------------------------------------------------------------

local home = os.getenv("HOME") or "/home/celestia"
local celxx = home .. "/.local/share/celestia/extras/opssat/celxx"

package.path = package.path
    .. ";" .. celxx .. "/?.lua"
    .. ";" .. home .. "/.local/share/celestia/extras/?.lua"

-- LuaSocket, for the module that talks to the simulator.
package.path = package.path
    .. ";/usr/share/lua/5.1/?.lua;/usr/share/lua/5.1/?/init.lua"
package.cpath = package.cpath
    .. ";/usr/lib/x86_64-linux-gnu/lua/5.1/?.so"

-- Loading it here as well as leaving it to the spacecraft definition means the
-- module is in place whichever of the two happens first.
pcall(require, "orbitattitude-realtime")


