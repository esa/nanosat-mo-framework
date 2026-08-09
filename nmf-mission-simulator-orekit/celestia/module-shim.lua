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
-- Stands in for the module the spacecraft definition asks for, at the only
-- places Celestia looks for it.
--
-- Celestia searches "?.lua" and "celxx/?.lua", both relative to its working
-- directory, which is the directory its own data lives in. Nothing mounted into
-- the container is on that path, so a copy of this file is placed at both of
-- those names when the image is built. All it does is read the real module out
-- of the add-on directory, which is mounted from the host and so can be edited
-- without building the image again.
-- ---------------------------------------------------------------------------

local home = os.getenv("HOME") or "/home/celestia"
local module = home .. "/.local/share/celestia/extras/opssat/celxx/orbitattitude-realtime.lua"

local chunk, err = loadfile(module)
if chunk == nil then
    io.write("orbitattitude-realtime: could not read ", module, ": ",
             tostring(err), "\n")
    -- Handing back an empty table leaves the spacecraft where it is rather
    -- than failing the definition that asked for this module.
    return {}
end

return chunk()
