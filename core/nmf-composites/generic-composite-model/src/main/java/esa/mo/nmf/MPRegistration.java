package esa.mo.nmf;

import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.callback.MPServiceOperationCallback;

public class MPRegistration {

    private final static URI PROVIDER_URI = new URI("NMF_Registration");

    /**
     * The possible registration modes.
     */
    public enum RegistrationMode {
        UPDATE_IF_EXISTS, DONT_UPDATE_IF_EXISTS
    };
    private RegistrationMode mode = RegistrationMode.DONT_UPDATE_IF_EXISTS; // default mode

    public final COMServicesProvider comServices;
    public final MPServiceOperationManager operationCallbackManager;

    public MPRegistration(
        COMServicesProvider comServices,
        MPServiceOperationManager operationCallbackManager
    ) {
        this.comServices = comServices;
        this.operationCallbackManager = operationCallbackManager;
    }

    public void setMode(RegistrationMode mode) {
        this.mode = mode;
    }

    /**
     * The registerRequestTemplates operation registers a set of Request Templates
     * in the Plan Information Management service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param requestTemplates
     * @return The request template object ids of the
     * RequestTemplate objects.
     */
    public ObjectIdList registerRequestTemplates(RequestTemplateDetailsList requestTemplates) {
        return new ObjectIdList();
    }

    public void registerOperation(MPServiceOperation operationName, MPServiceOperationCallback callback) {
        operationCallbackManager.register(operationName, callback);
    }
}