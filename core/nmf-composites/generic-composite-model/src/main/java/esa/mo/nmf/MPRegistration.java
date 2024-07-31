/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
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

    public MPRegistration(COMServicesProvider comServices, MPServiceOperationManager operationCallbackManager) {
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