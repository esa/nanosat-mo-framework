package esa.mo.helpertools.test.connections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import esa.mo.helpertools.connections.ServicesConnectionDetails;
import esa.mo.helpertools.connections.SingleConnectionDetails;

public class TestServicesConnectionDetails {

    @Test
    public void testLoadURIFromFiles() {
        ServicesConnectionDetails scd = new ServicesConnectionDetails();

        try {
            ServicesConnectionDetails res = scd.loadURIFromFiles(getClass().getClassLoader().getResource(
                "providerURIs.properties").getFile());
            HashMap<String, SingleConnectionDetails> resmap = res.getServices();
            Set<Entry<String, SingleConnectionDetails>> entryset = resmap.entrySet();
            List<SingleConnectionDetails> scdlist = entryset.stream().map(Entry::getValue).collect(Collectors.toList());
            assertEquals("maltcp://172.17.0.1:1024/nanosat-mo-supervisor-Archive", scdlist.get(0).getProviderURI()
                .getValue());
            assertEquals("maltcp://172.17.0.1:1024/nanosat-mo-supervisor-Event", scdlist.get(1).getProviderURI()
                .getValue());
        } catch (MalformedURLException | FileNotFoundException e) {
            e.printStackTrace();
            fail("Should not throw exception");
        }
    }

}
