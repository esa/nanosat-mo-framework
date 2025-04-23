package esa.mo.nmf.cmt.utils;

import java.io.IOException;

// TODO: implement Kubernetes API
public class KubernetesApi extends SimulatorApi {

    public KubernetesApi() {
    }

    @Override
    public void run(String name, String[] keplerElements) throws IOException {

    }

    public void start(String name) throws IOException {
    }

    @Override
    public void stop(String name) throws IOException {

    }

    public String getIPAddress(String name) throws IOException {
        return null;
    }

    public void remove(String name) throws IOException {
    }

    @Override
    public String getLogs(String name) throws IOException {
        return "";
    }
}
