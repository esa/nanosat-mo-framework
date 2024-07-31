package esa.mo.com.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

import esa.mo.com.impl.provider.ArchivePersistenceObject;

/*
 * Common interface implemented by the adapters used by getFromArchive helper
 */
public interface HelperArchiveRetrieveAdapterInterface {

    public ObjectType getObjType();

    public IdentifierList getDomain();

    public ElementList getObjectBodyList();

    public ArchiveDetailsList getArchiveDetailsList();

    public void waitUntilReady();

    public default List<ArchivePersistenceObject> getPersistenceObjectList() {
        ElementList obj = getObjectBodyList();
        ArchiveDetailsList objDetails = getArchiveDetailsList();
        if (objDetails == null || obj == null) {
            return null;
        }
        List<ArchivePersistenceObject> ret = new ArrayList<>();

        for (int i = 0; i < objDetails.size(); i++) {
            ArchivePersistenceObject tmp = new ArchivePersistenceObject(getObjType(), getDomain(),
                ((ArchiveDetails) objDetails.get(i)).getInstId(), (ArchiveDetails) objDetails.get(i), (Element) obj.get(
                    i));

            ret.add(tmp);
        }

        return ret;
    }
}
