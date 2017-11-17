/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.com.impl.sync;

import esa.mo.com.impl.archive.encoding.BinaryDecoder;
import esa.mo.com.impl.archive.encoding.BinaryEncoder;
import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.provider.ArchiveManager;
import esa.mo.com.impl.provider.ArchiveSyncProviderServiceImpl;
import esa.mo.com.impl.util.COMObjectStructure;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archivesync.consumer.ArchiveSyncStub;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class EncodeDecode {

    public static byte[] encodeToByteArray(final COMObjectEntity entity,
            ArchiveManager manager, Dictionary dictionary) {
        try {
            final ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
            final BinaryEncoder be = new BinaryEncoder(bodyBaos);

            Identifier network = manager.getFastNetwork().getNetwork(entity.getNetwork());
            Integer wordId1 = dictionary.getWordId(network.getValue());
            be.encodeShort(wordId1.shortValue());

            URI providerURI = manager.getFastProviderURI().getProviderURI(entity.getProviderURI());
            Integer wordId2 = dictionary.getWordId(providerURI.getValue());
            be.encodeShort(wordId2.shortValue());

            ObjectType objType = manager.getFastObjectType().getObjectType(entity.getObjectTypeId());
            be.encodeElement(objType);

            // --- Source Link ---
            if (entity.getSourceLink().getDomainId() == null) {
                be.encodeNullableShort(null);
            } else {
                IdentifierList sourceDomain = manager.getFastDomain().getDomain(entity.getSourceLink().getDomainId());
                Integer wordId3 = dictionary.getWordId(sourceDomain.toString());
                be.encodeNullableShort(wordId3.shortValue());
            }

            if (entity.getSourceLink().getObjectTypeId() == null) {
                be.encodeNullableElement(null);
            } else {
                ObjectType sourceObjType = manager.getFastObjectType().getObjectType(entity.getSourceLink().getObjectTypeId());
                be.encodeNullableElement(sourceObjType);
            }

            if (entity.getSourceLink().getObjectTypeId() == null) {
                be.encodeNullableLong(null);
            } else {
                Long sourceObjId = entity.getSourceLink().getObjId();
                be.encodeNullableLong(sourceObjId);
            }
            // -------------------

            Long relatedLink = entity.getRelatedLink();
            be.encodeNullableLong(relatedLink);

            byte[] array = entity.getObjectEncoded();
            Blob value = (array == null) ? null : new Blob(array);
            be.encodeNullableBlob(value);

            be.encodeLong(entity.getObjectId());
            be.encodeFineTime(entity.getTimestamp());

            byte[] output = bodyBaos.toByteArray();
            be.close();

            return output;
        } catch (Exception ex) {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new byte[0]; // Return an empty byte array
    }

    public static ArrayList<COMObjectStructure> decodeFromByteArrayList(ArrayList<byte[]> chunks,
            Dictionary dictionary, ArchiveSyncStub archiveSyncService, IdentifierList domain) {
        ArrayList<COMObjectStructure> objs = new ArrayList<COMObjectStructure>();

        if (chunks.isEmpty()) {
            return null;
        }

        int chunkSize = chunks.get(0).length; // We assume all chunks have the same size
        int totalSize = chunkSize * (chunks.size() - 1) + chunks.get(chunks.size() - 1).length;
        byte[] myArray = new byte[totalSize];

        for (int i = 0; i < totalSize; i++) {
            int slot = i % chunkSize;
            int chunkNumber = i / chunkSize;
            myArray[i] = chunks.get(chunkNumber)[slot];
        }

        final BinaryDecoder bd = new BinaryDecoder(myArray);
        boolean stillDecoding = true;

        while (stillDecoding) {
            try {
                Short wordIdNet = bd.decodeShort();
                Short wordIdProv = bd.decodeShort();
                ObjectType objType = (ObjectType) bd.decodeElement(new ObjectType());

                // --- Source Link ---
                Short sourceDomainId = bd.decodeNullableShort();
                ObjectType sourceObjType = (ObjectType) bd.decodeNullableElement(new ObjectType());
                Long sourceObjId = bd.decodeNullableLong();
                // -------------------

                Long relatedLink = bd.decodeNullableLong();
                Blob blob = bd.decodeNullableBlob();
                Element elem = null;

                if (blob != null) {
                    try {
                        final BinaryDecoder binDec = new BinaryDecoder(blob.getValue());
                        final MALElementFactory eleFact = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(binDec.decodeLong());
                        elem = binDec.decodeNullableElement((Element) eleFact.createElement());
                    } catch (MALException ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                                "The object body could not be decoded! Usually happens when there's "
                                + "an update in the APIs. (1) ", ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                                "The object body could not be decoded! Usually happens when there's "
                                + "an update in the APIs. (2) ", ex);
                    } catch (Exception ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                                "The object body could not be decoded! Usually happens when there's "
                                + "an update in the APIs. (3) ", ex);
                    }
                }

                Long objId = bd.decodeLong();
                FineTime timestamp = bd.decodeFineTime();

                IntegerList ids = new IntegerList();

                if (!dictionary.exists((int) wordIdNet)) {
                    ids.add((int) wordIdNet);
                }

                if (!dictionary.exists((int) wordIdProv)) {
                    ids.add((int) wordIdProv);
                }

                if (sourceDomainId != null) {
                    if (!dictionary.exists((int) sourceDomainId)) {
                        ids.add((int) sourceDomainId);
                    }
                }

                if (!ids.isEmpty()) {
                    // Then request the dictionary from the provider side!
                    StringList strings = archiveSyncService.getDictionary(ids);

                    for (int j = 0; j < ids.size(); j++) {
                        dictionary.defineWord(ids.get(j), strings.get(j));
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.FINE,
                                "Defining id: " + ids.get(j) + " as word: " + strings.get(j));
                    }
                }

                Identifier network = new Identifier(dictionary.getWord((int) wordIdNet));
                URI providerURI = new URI(dictionary.getWord((int) wordIdProv));
                IdentifierList sourceDomain;
                if (sourceDomainId != null) {
                    sourceDomain = HelperMisc.domainId2domain(dictionary.getWord((int) sourceDomainId));
                } else {
                    sourceDomain = null;
                }

                ObjectId objectId = (sourceObjType == null)
                        ? null
                        : new ObjectId(sourceObjType, new ObjectKey(sourceDomain, sourceObjId));

                ObjectDetails objDetails = new ObjectDetails(relatedLink, objectId);

                ArchiveDetails archDetails = new ArchiveDetails(objId, objDetails,
                        network, timestamp, providerURI);

                objs.add(new COMObjectStructure(domain, objType, archDetails, elem));
            } catch (ArrayIndexOutOfBoundsException ex) {
                stillDecoding = false;
            } catch (MALException ex) {
                Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return objs; // Return the list of objects
    }

}
