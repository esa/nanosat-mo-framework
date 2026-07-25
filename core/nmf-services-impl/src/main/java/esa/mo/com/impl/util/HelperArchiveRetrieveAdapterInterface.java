package esa.mo.com.impl.util;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.com.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/*
 * Common interface implemented by the adapters used by getFromArchive helper
 */
/**
 * Monitor and Control adapter for this application.
 */
public interface HelperArchiveRetrieveAdapterInterface {

    /**
     * Returns the obj type.
     *
     * @return the obj type
     */
    public ObjectType getObjType();

    /**
     * Returns the domain.
     *
     * @return the domain
     */
    public IdentifierList getDomain();

    /**
     * Returns the object body list.
     *
     * @return the object body list
     */
    public ElementList getObjectBodyList();

    /**
     * Returns the archive details list.
     *
     * @return the archive details list
     */
    public ArchiveDetailsList getArchiveDetailsList();

    /**
     * Blocks until the retrieval has completed.
     */
    public void waitUntilReady();

    /**
     * Returns the persistence object list.
     * @return the persistence object list
     */
    public default List<ArchivePersistenceObject> getPersistenceObjectList() {
        ElementList obj = getObjectBodyList();
        ArchiveDetailsList objDetails = getArchiveDetailsList();
        if (objDetails == null || obj == null) {
            return null;
        }
        List<ArchivePersistenceObject> ret = new ArrayList<>();

        for (int i = 0; i < objDetails.size(); i++) {
            ArchivePersistenceObject tmp = new ArchivePersistenceObject(
                    getObjType(),
                    getDomain(),
                    ((ArchiveDetails) objDetails.get(i)).getId(),
                    (ArchiveDetails) objDetails.get(i),
                    (Element) obj.get(i));

            ret.add(tmp);
        }

        return ret;
    }
}
