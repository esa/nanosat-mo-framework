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
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
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

    public static ArrayList<COMObjectEntity> decodeFromByteArrayList(
            final ArrayList<byte[]> chunks, Dictionary dictionary) {
        ArrayList<COMObjectEntity> objs = new ArrayList<COMObjectEntity>();

        if (chunks.isEmpty()) {
            return null;
        }

        int chunkSize = chunks.get(0).length; // We assume all chunks have the same size
        int totalSize = chunkSize * (chunks.size() - 1) + chunks.get(chunks.size()-1).length;
        byte[] myArray = new byte[totalSize];

        for (int i = 0; i < totalSize; i++) {
            int slot = i % chunkSize;
            int chunkNumber = i / chunkSize;
            myArray[i] = chunks.get(chunkNumber)[slot];
        }

        final BinaryDecoder bd = new BinaryDecoder(myArray);

        for (int i = 0; i < totalSize; i++) {
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

                Identifier network = new Identifier(dictionary.getWord((int) wordIdNet));
                URI providerURI = new URI(dictionary.getWord((int) wordIdProv));
                IdentifierList sourceDomain = HelperMisc.domainId2domain(dictionary.getWord((int) sourceDomainId));
                
                /*
                COMObjectEntity obj = new     COMObjectEntity(
                Integer objectTypeId,
                Integer domain,
                Long objId,
                Long timestampArchiveDetails,
                Integer providerURI,
                Integer network,
                SourceLinkContainer sourceLink,
                Long relatedLink,
                Object object) {;
                }
                 */
            } catch (MALException ex) {
                Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return objs; // Return the list of objects
    }

}
