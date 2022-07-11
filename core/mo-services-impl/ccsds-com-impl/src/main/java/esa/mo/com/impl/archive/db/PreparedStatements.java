package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatements {

    Connection c;

    public PreparedStatements(Connection serverConnection) {
        c = serverConnection;
    }

    private static String SELECT_ALL_COM_OBJECT_IDS = "SELECT objId " + "FROM COMObjectEntity "
            + "WHERE ((objectTypeId = ?) AND (domainId = ?))";
    private static String SELECT_COM_OBJECTS = "SELECT objectTypeId, domainId, objId, "
            + "timestampArchiveDetails, providerURI, network, sourceLinkObjectTypeId, "
            + "sourceLinkDomainId, sourceLinkObjId, relatedLink, objBody " + "FROM COMObjectEntity "
            + "WHERE ((objectTypeId = ?) AND (domainId = ?) AND (objId in (%s)))";
    private static String INSERT_COM_OBJECTS = "INSERT INTO COMObjectEntity "
            + "(objectTypeId, objId, domainId, network, objBody, providerURI, relatedLink, "
            + "sourceLinkDomainId, sourceLinkObjId, sourceLinkObjectTypeId, timestampArchiveDetails) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static String DELETE_COM_OBJECTS = "DELETE FROM COMObjectEntity "
            + "WHERE (((objectTypeId = ?) AND (domainId = ?) AND (objId = ?)))";
    private static String UPDATE_COM_OBJECTS = "UPDATE COMObjectEntity "
            + "SET objectTypeId = ?, objId = ?, domainId = ?, network = ?, objBody = ?, "
            + "providerURI = ?, relatedLink = ?, sourceLinkDomainId = ?, "
            + "sourceLinkObjId = ?, sourceLinkObjectTypeId = ?, timestampArchiveDetails = ? "
            + "WHERE (((objectTypeId = ?) AND (domainId = ?) AND (objId = ?)));";
    private static String SELECT_MAX_OBJ_ID = "SELECT MAX(objId) FROM COMObjectEntity WHERE ((objectTypeId = ?) AND (domainId = ?))";

    private PreparedStatement selectAllCOMObjectIds;
    private PreparedStatement insertCOMObjects;
    private PreparedStatement deleteCOMObjects;
    private PreparedStatement updateCOMObjects;
    private PreparedStatement selectMaxObjId;

    public void init(boolean isPostgres) throws SQLException {
        if(isPostgres) {
            SELECT_COM_OBJECTS = "SELECT objectTypeId, domainId, objId, "
                                 + "timestampArchiveDetails, providerURI, network, sourceLinkObjectTypeId, "
                                 + "sourceLinkDomainId, sourceLinkObjId, relatedLink, objBody " + "FROM COMObjectEntity "
                                 + "WHERE ((objectTypeId = ?) AND (domainId = ?) AND (objId = ANY(?)))";
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

    public String getSelectCOMObjectsQueryString() {
        return SELECT_COM_OBJECTS;
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
