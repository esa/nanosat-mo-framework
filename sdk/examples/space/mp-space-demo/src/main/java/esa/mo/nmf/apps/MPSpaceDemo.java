package esa.mo.nmf.apps;

import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;

/**
 * An NMF App that demonstrates MP services
 */
public class MPSpaceDemo {

    private final NanoSatMOConnectorImpl connector;

    public MPSpaceDemo() {
        MPSpaceDemoAdapter adapter = new MPSpaceDemoAdapter();
        connector = new NanoSatMOConnectorImpl();
        adapter.setNMF(connector);
        connector.init(adapter);
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        MPSpaceDemo test = new MPSpaceDemo();
    }
}
