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
package esa.mo.com.impl.provider;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Lukasz.Stochlak
 */
public class ArchiveManagerTest {

    public static final int NUMBER_OF_OBJECTS = 10000;

    @Test
    @Ignore("Manual test only")
    public void testFastObjects() throws Exception {
        Map<String, Long> times = new HashMap<>();

        System.out.println("ArchiveManager - Fast Objects test >> BEGIN <<");

        Date testBegin = new Date();

        esa.mo.com.impl.provider.EventProviderServiceImpl eventProviderServiceImpl0 = null;

        esa.mo.com.impl.provider.ArchiveManager archiveManager1 = new esa.mo.com.impl.provider.ArchiveManager(
            eventProviderServiceImpl0);

        Date start = new Date();

        archiveManager1.init();

        Date stop = new Date();

        times.put("00. Init time : ", (stop.getTime() - start.getTime()));

        start = new Date();

        IdentifierList domain;

        long insertTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            domain = new IdentifierList();
            domain.add(new Identifier("test_1_" + i));
            domain.add(new Identifier("test_2_" + i));
            domain.add(new Identifier("test_3_" + i));
            Date writeStart = new Date();
            archiveManager1.getFastDomain().getDomainId(domain);
            Date writeStop = new Date();

            insertTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("01. Domain insert average time : ", insertTime / NUMBER_OF_OBJECTS);

        insertTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastNetwork().getNetworkId(new Identifier("network_" + i));
            Date writeStop = new Date();

            insertTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("02. Network insert average time : ", insertTime / NUMBER_OF_OBJECTS);

        insertTime = 0;

        for (int i = 0; i < 10000; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastObjectType().getObjectTypeId(new ObjectType(new UShort(i), new UShort(i), new UOctet(
                (short) (i % 255)), new UShort(i)));
            Date writeStop = new Date();

            insertTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("03. Object type insert average time : ", insertTime / NUMBER_OF_OBJECTS);

        insertTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastProviderURI().getProviderURIId(new URI("maltcp://" + "test_1_" + i + ".test_2_" + i +
                ".test_3_" + i));
            Date writeStop = new Date();

            insertTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("04. URI insert average time : ", insertTime / NUMBER_OF_OBJECTS);

        stop = new Date();

        times.put("05. Filling time : ", (stop.getTime() - start.getTime()));

        long readTime = 0;

        start = new Date();

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            domain = new IdentifierList();
            domain.add(new Identifier("test_1_" + i));
            domain.add(new Identifier("test_2_" + i));
            domain.add(new Identifier("test_3_" + i));
            Date writeStart = new Date();
            archiveManager1.getFastDomain().getDomainId(domain);
            Date writeStop = new Date();

            readTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("06. Domain read average time : ", readTime / NUMBER_OF_OBJECTS);

        readTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastNetwork().getNetworkId(new Identifier("network_" + i));
            Date writeStop = new Date();

            readTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("07. Network read average time : ", readTime / NUMBER_OF_OBJECTS);

        readTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastObjectType().getObjectTypeId(new ObjectType(new UShort(i), new UShort(i), new UOctet(
                (short) (i % 255)), new UShort(i)));
            Date writeStop = new Date();

            readTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("08. Object type read average time : ", readTime / NUMBER_OF_OBJECTS);

        readTime = 0;

        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Date writeStart = new Date();
            archiveManager1.getFastProviderURI().getProviderURIId(new URI("maltcp://" + "test_1_" + i + ".test_2_" + i +
                ".test_3_" + i));
            Date writeStop = new Date();

            readTime += (writeStop.getTime() - writeStart.getTime());
        }

        times.put("09. URI read average time : ", readTime / NUMBER_OF_OBJECTS);

        stop = new Date();

        times.put("10. Reading time : ", (stop.getTime() - start.getTime()));

        Date testEnd = new Date();

        Set<Entry<String, Long>> set = times.entrySet();
        List<Entry<String, Long>> sorted = set.stream().sorted(Comparator.comparing(Entry<String, Long>::getKey))
            .collect(Collectors.toList());

        for (Entry<String, Long> e : sorted) {
            System.out.println(e.getKey() + e.getValue());
        }

        System.out.println("ArchiveManager - Fast Objects test >> END << - time: " + (testEnd.getTime() - testBegin
            .getTime()) + " ms");

    }
}
//------------------------------------------------------------------------------
