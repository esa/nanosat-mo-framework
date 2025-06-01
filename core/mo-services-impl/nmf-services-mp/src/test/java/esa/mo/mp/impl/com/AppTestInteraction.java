package esa.mo.mp.impl.com;

import java.util.Map;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * AppTestInteraction is used by tests to mock MALInteraction
 */
public class AppTestInteraction implements MALInteraction {

    @Override
    public MALMessageHeader getMessageHeader() {
        return new AppTestMessageHeader();
    }

    @Override
    public MALOperation getOperation() {
        return null;
    }

    @Override
    public void setQoSProperty(String name, Object value) throws IllegalArgumentException {
    }

    @Override
    public Object getQoSProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Map<String, Object> getQoSProperties() {
        return null;
    }
}
