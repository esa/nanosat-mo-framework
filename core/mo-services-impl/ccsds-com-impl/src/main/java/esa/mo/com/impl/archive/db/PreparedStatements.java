/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatements {

    Connection c;

    public PreparedStatements(Connection serverConnection) {
        c = serverConnection;
    }

    private final static String SELECT_ALL_COM_OBJECT_IDS = "SELECT objId " + "FROM COMObjectEntity " +
        "WHERE ((objectTypeId = ?) AND (domainId = ?))";
    private final static String SELECT_COM_OBJECTS = "SELECT objectTypeId, domainId, objId, " +
        "timestampArchiveDetails, providerURI, network, sourceLinkObjectTypeId, " +
        "sourceLinkDomainId, sourceLinkObjId, relatedLink, objBody " + "FROM COMObjectEntity " +
        "WHERE ((objectTypeId = ?) AND (domainId = ?) AND (objId = ANY(?)))";
    private final static String INSERT_COM_OBJECTS = "INSERT INTO COMObjectEntity " +
        "(objectTypeId, objId, domainId, network, objBody, providerURI, relatedLink, " +
        "sourceLinkDomainId, sourceLinkObjId, sourceLinkObjectTypeId, timestampArchiveDetails) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String DELETE_COM_OBJECTS = "DELETE FROM COMObjectEntity " +
        "WHERE (((objectTypeId = ?) AND (domainId = ?) AND (objId = ?)))";
    private final static String UPDATE_COM_OBJECTS = "UPDATE COMObjectEntity " +
        "SET objectTypeId = ?, objId = ?, domainId = ?, network = ?, objBody = ?, " +
        "providerURI = ?, relatedLink = ?, sourceLinkDomainId = ?, " +
        "sourceLinkObjId = ?, sourceLinkObjectTypeId = ?, timestampArchiveDetails = ? " +
        "WHERE (((objectTypeId = ?) AND (domainId = ?) AND (objId = ?)));";
    private final static String SELECT_MAX_OBJ_ID = "SELECT MAX(objId) FROM COMObjectEntity WHERE ((objectTypeId = ?) AND (domainId = ?))";

    private PreparedStatement selectAllCOMObjectIds;
    // select com objects prepared statement is available only for postgres because sqlite
    // doesn't support binding arrays to query parameters
    private PreparedStatement selectCOMObjects;
    private PreparedStatement insertCOMObjects;
    private PreparedStatement deleteCOMObjects;
    private PreparedStatement updateCOMObjects;
    private PreparedStatement selectMaxObjId;

    public void init(boolean isPostgres) throws SQLException {
        if (isPostgres) {
            selectCOMObjects = c.prepareStatement(SELECT_COM_OBJECTS);
        }
        selectAllCOMObjectIds = c.prepareStatement(SELECT_ALL_COM_OBJECT_IDS);
        insertCOMObjects = c.prepareStatement(INSERT_COM_OBJECTS);
        deleteCOMObjects = c.prepareStatement(DELETE_COM_OBJECTS);
        updateCOMObjects = c.prepareStatement(UPDATE_COM_OBJECTS);
        selectMaxObjId = c.prepareStatement(SELECT_MAX_OBJ_ID);
    }

    public PreparedStatement getSelectAllCOMObjects() {
        return this.selectAllCOMObjectIds;
    }

    public PreparedStatement getSelectCOMObjects() {
        return selectCOMObjects;
    }

    public PreparedStatement getInsertCOMObjects() {
        return this.insertCOMObjects;
    }

    public PreparedStatement getDeleteCOMObjects() {
        return this.deleteCOMObjects;
    }

    public PreparedStatement getUpdateCOMObjects() {
        return this.updateCOMObjects;
    }

    public PreparedStatement getSelectMaxObjId() {
        return this.selectMaxObjId;
    }

}
