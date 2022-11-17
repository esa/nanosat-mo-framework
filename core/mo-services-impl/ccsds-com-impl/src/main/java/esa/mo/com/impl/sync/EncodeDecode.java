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
package esa.mo.com.impl.sync;

import esa.mo.com.impl.archive.encoding.BinaryDecoder;
import esa.mo.com.impl.archive.encoding.BinaryEncoder;
import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.provider.ArchiveManager;
import esa.mo.com.impl.util.COMObjectStructure;
import esa.mo.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archivesync.consumer.ArchiveSyncStub;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Encodes and decodes COM objects to and from bytes.
 *
 * @author Cesar Coelho
 */
public class EncodeDecode {

    private static final Logger LOGGER = Logger.getLogger(EncodeDecode.class.getName());

    /**
     * Encodes a database COM object to byte array.
     *
     * @param entity The object to encode
     * @param manager The archive manager for fast object details retrieval
     * @param dictionary Dictionary mapping strings to integers
     * @return The byte array holding the encoded COM object
     */
    public static byte[] encodeToByteArray(final COMObjectEntity entity, ArchiveManager manager,
        Dictionary dictionary) {
        try {
            final ByteArrayOutputStream outputBytesStream = new ByteArrayOutputStream();
            final BinaryEncoder encoder = new BinaryEncoder(outputBytesStream);

            encodeEntity(entity, manager, dictionary, encoder);

            byte[] output = outputBytesStream.toByteArray();
            encoder.close();

            return output;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return new byte[0]; // Return an empty byte array
    }

    /**
     * Decodes a list of COM objects from a list of byte arrays.
     *
     * @param chunks The list of byte arrays
     * @param dictionary Local dictionary mapping integers to strings
     * @param archiveSyncService ArchiveSync provider to fetch strings missing in the local dictionary
     * @param domain The domain of the COM objects to decode
     * @return The list of decoded COM objects
     */
    public static ArrayList<COMObjectStructure> decodeFromByteArrayList(List<byte[]> chunks, Dictionary dictionary,
        ArchiveSyncStub archiveSyncService, IdentifierList domain) {
        if (chunks.isEmpty()) {
            return null;
        }

        int chunkSize = chunks.get(0).length; // We assume all chunks have the same size
        int totalSize = chunkSize * (chunks.size() - 1) + chunks.get(chunks.size() - 1).length;
        byte[] bytes = new byte[totalSize];

        for (int i = 0; i < totalSize; i++) {
            int slot = i % chunkSize;
            int chunkNumber = i / chunkSize;
            bytes[i] = chunks.get(chunkNumber)[slot];
        }

        return decodeFromByteArray(dictionary, archiveSyncService, domain, bytes);
    }

    public static byte[] encodeToCompressedByteArray(final List<COMObjectEntity> entities, ArchiveManager manager,
        Dictionary dictionary) {
        if (entities.isEmpty()) {
            return new byte[0];
        }
        try {
            ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
            BinaryEncoder encoder = new BinaryEncoder(bytesOutputStream);

            for (COMObjectEntity entity : entities) {
                encodeEntity(entity, manager, dictionary, encoder);
            }

            byte[] uncompressedOutput = bytesOutputStream.toByteArray();
            encoder.close();
            Logger.getLogger(EncodeDecode.class.getName()).log(Level.FINE, "Uncompressed objects size: " +
                uncompressedOutput.length + " bytes");

            ByteArrayOutputStream compressedBytesOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(compressedBytesOutputStream);
            gzip.write(uncompressedOutput);
            gzip.close();
            byte[] compressedBytes = compressedBytesOutputStream.toByteArray();
            compressedBytesOutputStream.close();

            byte[] uncompressedSize = ByteBuffer.allocate(4).putInt(uncompressedOutput.length).array();
            byte[] output = new byte[4 + compressedBytes.length];

            System.arraycopy(uncompressedSize, 0, output, 0, 4);
            System.arraycopy(compressedBytes, 0, output, 4, compressedBytes.length);

            Logger.getLogger(EncodeDecode.class.getName()).log(Level.FINE, "Compressed objects size: " +
                compressedBytes.length + " bytes");

            return output;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return new byte[0]; // Return an empty byte array
    }

    public static ArrayList<COMObjectStructure> decodeFromCompressedByteArrayList(List<byte[]> chunks,
        Dictionary dictionary, ArchiveSyncStub archiveSyncService, IdentifierList domain) {
        if (chunks.isEmpty()) {
            return new ArrayList<>();
        }

        int chunkSize = chunks.get(0).length; // We assume all chunks have the same size
        int totalCompressedSize = chunkSize * (chunks.size() - 1) + chunks.get(chunks.size() - 1).length;

        byte[] compressedBytes = new byte[totalCompressedSize - 4];

        byte[] uncompressedSizeBytes = new byte[4]; // first 4 bytes are the size of uncompressed data
        System.arraycopy(chunks.get(0), 0, uncompressedSizeBytes, 0, 4);
        int uncompressedSize = ByteBuffer.wrap(uncompressedSizeBytes).getInt();

        for (int i = 0; i < totalCompressedSize - 4; i++) {
            int slot = (i + 4) % chunkSize;
            int chunkNumber = (i + 4) / chunkSize;
            compressedBytes[i] = chunks.get(chunkNumber)[slot];
        }

        ByteArrayInputStream bytesInputStream = new ByteArrayInputStream(compressedBytes);
        byte[] bytes = new byte[uncompressedSize];
        try {
            GZIPInputStream gzip = new GZIPInputStream(bytesInputStream, uncompressedSize);
            gzip.read(bytes);
            gzip.close();
            bytesInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return decodeFromByteArray(dictionary, archiveSyncService, domain, bytes);
    }

    private static void encodeEntity(COMObjectEntity entity, ArchiveManager manager, Dictionary dictionary,
        BinaryEncoder encoder) throws Exception {
        Identifier network = manager.getFastNetwork().getNetwork(entity.getNetwork());
        Integer networkId = dictionary.getWordId(network.getValue());
        encoder.encodeShort(networkId.shortValue());

        URI providerURI = manager.getFastProviderURI().getProviderURI(entity.getProviderURI());
        Integer providerURIId = dictionary.getWordId(providerURI.getValue());
        encoder.encodeShort(providerURIId.shortValue());

        ObjectType objType = manager.getFastObjectType().getObjectType(entity.getObjectTypeId());
        encoder.encodeElement(objType);

        // --- Source Link ---
        if (entity.getSourceLink().getDomainId() == null) {
            encoder.encodeNullableShort(null);
        } else {
            IdentifierList sourceDomain = manager.getFastDomain().getDomain(entity.getSourceLink().getDomainId());
            Integer sourceDomainId = dictionary.getWordId(HelperMisc.domain2domainId(sourceDomain));
            encoder.encodeNullableShort(sourceDomainId.shortValue());
        }

        if (entity.getSourceLink().getObjectTypeId() == null) {
            encoder.encodeNullableElement(null);
        } else {
            ObjectType sourceObjType = manager.getFastObjectType().getObjectType(entity.getSourceLink()
                .getObjectTypeId());
            encoder.encodeNullableElement(sourceObjType);
        }

        if (entity.getSourceLink().getObjectTypeId() == null) {
            encoder.encodeNullableLong(null);
        } else {
            Long sourceObjId = entity.getSourceLink().getObjId();
            encoder.encodeNullableLong(sourceObjId);
        }
        // -------------------

        Long relatedLink = entity.getRelatedLink();
        encoder.encodeNullableLong(relatedLink);

        byte[] array = entity.getObjectEncoded();
        Blob value = (array == null) ? null : new Blob(array);
        encoder.encodeNullableBlob(value);

        encoder.encodeLong(entity.getObjectId());
        encoder.encodeFineTime(entity.getTimestamp());
    }

    private static ArrayList<COMObjectStructure> decodeFromByteArray(Dictionary dictionary,
        ArchiveSyncStub archiveSyncService, IdentifierList domain, byte[] bytes) {
        ArrayList<COMObjectStructure> objs = new ArrayList<>();
        final BinaryDecoder decoder = new BinaryDecoder(bytes);
        boolean stillDecoding = true;

        while (stillDecoding) {
            try {
                Short networkId = decoder.decodeShort();
                Short providerURIId = decoder.decodeShort();
                ObjectType objType = (ObjectType) decoder.decodeElement(new ObjectType());

                // --- Source Link ---
                Short sourceDomainId = decoder.decodeNullableShort();
                ObjectType sourceObjType = (ObjectType) decoder.decodeNullableElement(new ObjectType());
                Long sourceObjId = decoder.decodeNullableLong();
                // -------------------

                Long relatedLink = decoder.decodeNullableLong();
                Blob blob = decoder.decodeNullableBlob();
                Element element = null;

                if (blob != null) {
                    try {
                        final BinaryDecoder blobDecoder = new BinaryDecoder(blob.getValue());
                        final MALElementFactory elementFactory = MALContextFactory.getElementFactoryRegistry()
                            .lookupElementFactory(blobDecoder.decodeLong());
                        element = blobDecoder.decodeNullableElement((Element) elementFactory.createElement());
                    } catch (MALException ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                            "The object body could not be decoded! Usually happens when there's " +
                                "an update in the APIs. (1) ", ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                            "The object body could not be decoded! Usually happens when there's " +
                                "an update in the APIs. (2) ", ex);
                    } catch (Exception ex) {
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.SEVERE,
                            "The object body could not be decoded! Usually happens when there's " +
                                "an update in the APIs. (3) ", ex);
                    }
                }

                Long objId = decoder.decodeLong();
                FineTime timestamp = decoder.decodeFineTime();

                IntegerList ids = new IntegerList();

                if (!dictionary.exists((int) networkId)) {
                    ids.add((int) networkId);
                }

                if (!dictionary.exists((int) providerURIId)) {
                    ids.add((int) providerURIId);
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
                        Logger.getLogger(COMObjectEntity.class.getName()).log(Level.FINE, "Defining id: " + ids.get(j) +
                            " as word: " + strings.get(j));
                    }
                }

                Identifier network = new Identifier(dictionary.getWord((int) networkId));
                URI providerURI = new URI(dictionary.getWord((int) providerURIId));
                IdentifierList sourceDomain;
                if (sourceDomainId != null) {
                    sourceDomain = HelperMisc.domainId2domain(dictionary.getWord((int) sourceDomainId));
                } else {
                    sourceDomain = null;
                }

                ObjectId objectId = (sourceObjType == null) ? null : new ObjectId(sourceObjType, new ObjectKey(
                    sourceDomain, sourceObjId));

                ObjectDetails objDetails = new ObjectDetails(relatedLink, objectId);

                ArchiveDetails archDetails = new ArchiveDetails(objId, objDetails, network, timestamp, providerURI);

                objs.add(new COMObjectStructure(domain, objType, archDetails, element));
            } catch (IndexOutOfBoundsException ex) {
                stillDecoding = false;
            } catch (Exception ex) {
                Logger.getLogger(EncodeDecode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return objs; // Return the list of objects
    }

}
