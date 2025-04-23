package esa.mo.nmf.cmt.utils;

import java.io.IOException;

/**
 * This abstract class is used to provide multiple APIs for simulating the
 * NanoSat segments. Implement this class to create a new API.
 */
public abstract class SimulatorApi {

    public abstract void run(String name, String[] keplerElements) throws IOException;

    public abstract void start(String name) throws IOException;

    public abstract void stop(String name) throws IOException;

    public abstract String getIPAddress(String name) throws IOException;

    public abstract void remove(String name) throws IOException;

    public abstract String getLogs(String name) throws IOException;

}
