/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that the objects ArchiveSync sends come back as they were sent.
 *
 * The objects are compressed and sent in chunks. Data that compresses little,
 * or not at all, such as a picture, is larger compressed than it was before:
 * a single read of the inflated stream then returns before the end of it, and
 * the bytes it did not reach were left as zeros.
 */
public class CompressionTest {

    private static final int[] SIZES = {1, 10, 100, 1000, 100000};
    private static final int[] CHUNK_SIZES = {5, 64, 4096};

    @Test
    public void dataThatDoesNotCompressComesBackWhole() throws IOException {
        Random random = new Random(42);
        for (int size : SIZES) {
            byte[] data = new byte[size];
            random.nextBytes(data);
            assertRoundTrip(data);
        }
    }

    @Test
    public void dataThatCompressesComesBackWhole() throws IOException {
        for (int size : SIZES) {
            byte[] data = new byte[size];
            for (int i = 0; i < size; i++) {
                data[i] = (byte) (i % 7);
            }
            assertRoundTrip(data);
        }
    }

    private static void assertRoundTrip(byte[] data) throws IOException {
        byte[] compressed = EncodeDecode.compress(data);
        for (int chunkSize : CHUNK_SIZES) {
            byte[] decompressed = EncodeDecode.decompress(split(compressed, chunkSize));
            Assert.assertArrayEquals("size " + data.length + ", chunks of " + chunkSize, data, decompressed);
        }
    }

    private static List<byte[]> split(byte[] bytes, int chunkSize) {
        List<byte[]> chunks = new ArrayList<>();
        for (int from = 0; from < bytes.length; from += chunkSize) {
            chunks.add(Arrays.copyOfRange(bytes, from, Math.min(from + chunkSize, bytes.length)));
        }
        return chunks;
    }
}
