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
package esa.mo.nmf.nmfpackage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 *
 * @author Cesar Coelho
 */
public class HelperNMFPackage {

    public static final String RECEIPT_FILENAME = "nmfPackage.receipt";
    public static final String DS_FILENAME = "digitalSignature.key";
    public static final String PRIVATE_KEY_FILENAME = "privateKey.key";
    public static final String NMF_PACKAGE_DESCRIPTOR_VERSION = "NMFPackageDescriptorVersion=";

    public static long calculateCRC(final String filepath) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
        CRC32 crc = new CRC32();
        int cnt;

        while ((cnt = inputStream.read()) != -1) {
            crc.update(cnt);
        }
        
        return crc.getValue();
    }
    
}
