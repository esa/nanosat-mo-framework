/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.mcadapters;

import esa.mo.mc.impl.interfaces.ActionNotFoundException;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFProvider;
import java.lang.management.ManagementFactory;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;

/**
 * A default Supervisor MC adapter exposing basic information about the running
 * framework as read-only parameters. Part of the default MC set that every NMF
 * Supervisor provides, regardless of mission.
 *
 * @author Cesar Coelho
 */
public class SupervisorInfoMCAdapter extends MonitorAndControlNMFAdapter {

    private static final String PARAM_NMF_VERSION = "nmf.version";
    private static final String PARAM_NMF_UPTIME = "nmf.uptime";

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(new ParameterDefinition(new Identifier(PARAM_NMF_VERSION),
                "The version of the NMF framework.",
                AttributeType.STRING, false, new Duration(0), true));
        defs.add(new ParameterDefinition(new Identifier(PARAM_NMF_UPTIME),
                "The uptime of the Supervisor process, in seconds.",
                AttributeType.DOUBLE, false, new Duration(0), true));
        registration.registerParameters(defs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) {
        if (identifier == null) {
            return null;
        }
        switch (identifier.getValue()) {
            case PARAM_NMF_VERSION: {
                String version = NMFProvider.class.getPackage().getImplementationVersion();
                return (Attribute) Attribute.javaType2Attribute(version == null ? "unknown" : version);
            }
            case PARAM_NMF_UPTIME: {
                double seconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
                return (Attribute) Attribute.javaType2Attribute(seconds);
            }
            default:
                return null;
        }
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ActionNotFoundException {
        throw new ActionNotFoundException(name == null ? null : name.getValue());
    }
}
