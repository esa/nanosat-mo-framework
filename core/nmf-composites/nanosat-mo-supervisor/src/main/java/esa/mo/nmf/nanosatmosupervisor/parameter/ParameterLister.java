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
package esa.mo.nmf.nanosatmosupervisor.parameter;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.xml.sax.SAXException;
import esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the list of OBSW parameters from the datapool XML file and provides it as a map.
 *
 * @author Tanguy Soto
 */
public class ParameterLister {
    private static final Logger LOGGER = Logger.getLogger(MCSupervisorBasicAdapter.class.getName());

    /**
     * The map of OBSW parameters defined in datapool so they can be accessed by parameter name.
     */
    private final HashMap<Identifier, OBSWParameter> parameterMap;

    /**
     * Initializes this object using the contents of the provided datapool XML file.
     *
     * @param datapool Stream to read the datapool.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public ParameterLister(InputStream datapool) throws IOException, JAXBException, XMLStreamException {
        LOGGER.log(Level.INFO, "Loading OBSW parameters from datapool");
        this.parameterMap = readParameters(datapool);
    }

    /**
     * Returns the OBSW parameters in the datapool, mapped by OBSW parameter name.
     *
     * @return The parameters in the datapool.
     */
    public HashMap<Identifier, OBSWParameter> getParameters() {
        return this.parameterMap;
    }

    /**
     * Reads the OBSW parameters in the datapool XML file and returns them in a map by OBSW parameter
     * ID.
     *
     * @param datapool The input stream to read the XML file.
     * @return The parameters read from the XML.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private HashMap<Identifier, OBSWParameter> readParameters(InputStream datapool) throws IOException, JAXBException,
        XMLStreamException {
        HashMap<Identifier, OBSWParameter> map = new HashMap<>();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(datapool);

        JAXBContext jc = JAXBContext.newInstance(OBSWParameter.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        xsr.nextTag();
        while (xsr.hasNext()) {
            xsr.next();

            if (xsr.isStartElement() && xsr.getLocalName().equals("parameter")) {
                OBSWParameter parameter = unmarshaller.unmarshal(xsr, OBSWParameter.class).getValue();
                map.put(new Identifier(parameter.getName()), parameter);
            }
        }
        xsr.close();

        return map;
    }
}
