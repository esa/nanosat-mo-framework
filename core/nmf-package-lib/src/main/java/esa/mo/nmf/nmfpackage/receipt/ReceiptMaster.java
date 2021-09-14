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
package esa.mo.nmf.nmfpackage.receipt;

import esa.mo.nmf.nmfpackage.HelperNMFPackage;
import esa.mo.nmf.nmfpackage.descriptor.NMFPackageDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Cesar Coelho
 */
public class ReceiptMaster {

    public static void writeLatestReceipt(NMFPackageDescriptor descriptor,
            BufferedWriter bw) throws IOException {
        bw.write(HelperNMFPackage.NMF_PACKAGE_DESCRIPTOR_VERSION + "3");
        bw.newLine();
        ReceiptVersion3.writeReceipt(bw, descriptor);
        bw.flush();
    }

    public static NMFPackageDescriptor parseReceipt(final String version,
            final BufferedReader br) throws IOException {
        if ("1".equals(version)) {
            return ReceiptVersion1.readReceipt(br);
        }

        if ("2".equals(version)) {
            return ReceiptVersion2.readReceipt(br);
        }

        if ("3".equals(version)) {
            return ReceiptVersion3.readReceipt(br);
        } else {
            throw new IOException("Unknown version: " + version);
        }
    }

}
