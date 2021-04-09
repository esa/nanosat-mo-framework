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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
   * Tag to read elements
   */
  private static final String TAG_PARAMETER = "parameter";

  /**
   * XML attribute name
   */
  private static final String ATTRIBUTE_NAME = "name";

  /**
   * XML attribute name
   */
  private static final String ATTRIBUTE_TYPE = "attributeType";

  /**
   * XML attribute name
   */
  private static final String ATTRIBUTE_DESCRIPTION = "description";

  /**
   * XML attribute name
   */
  private static final String ATTRIBUTE_ID = "id";

  /**
   * XML attribute name
   */
  private static final String ATTRIBUTE_UNIT = "unit";

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
  public ParameterLister(InputStream datapool)
      throws ParserConfigurationException, SAXException, IOException {
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
  private HashMap<Identifier, OBSWParameter> readParameters(InputStream datapool)
      throws IOException, SAXException, ParserConfigurationException {
    HashMap<Identifier, OBSWParameter> map = new HashMap<>();

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(datapool);

    NodeList parameterNodeList = document.getElementsByTagName(TAG_PARAMETER);
    for (int i = 0; i < parameterNodeList.getLength(); i++) {
      Node parameterNode = parameterNodeList.item(i);
      if (Node.ELEMENT_NODE == parameterNode.getNodeType()) {
        Element parameterElement = (Element) parameterNode;

        String name = parameterElement.getAttribute(ATTRIBUTE_NAME);
        String malAttTypeName = parameterElement.getAttribute(ATTRIBUTE_TYPE);
        String description = parameterElement.getAttribute(ATTRIBUTE_DESCRIPTION);
        String id = parameterElement.getAttribute(ATTRIBUTE_ID);
        String unit = parameterElement.getAttribute(ATTRIBUTE_UNIT);

        OBSWParameter parameter =
            new OBSWParameter(Long.parseLong(id), name, description, malAttTypeName, unit);
        map.put(new Identifier(parameter.getName()), parameter);
      }
    }

    return map;
  }
}
