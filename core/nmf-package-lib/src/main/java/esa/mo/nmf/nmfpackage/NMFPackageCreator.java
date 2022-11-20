/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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

import esa.mo.nmf.nmfpackage.utils.HelperNMFPackage;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.nmfpackage.receipt.DetailsApp;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The NMF Package Creator class.
 *
 * @author Cesar Coelho
 */
public class NMFPackageCreator {




    @Deprecated
    public static String nmfPackageCreator(DetailsApp details,
            ArrayList<String> filesInput, ArrayList<String> newLocationsInput) {
        return NMFPackageCreator.nmfPackageCreator(details, filesInput, newLocationsInput, null);
    }

    @Deprecated
    public static String nmfPackageCreator(DetailsApp details,
            ArrayList<String> filesInput, ArrayList<String> newLocationsInput,
            String destinationFolder) {
        Metadata metadata = new Metadata(details.getProperties());
        return null;
        // return NMFPackageCreator.create(metadata, filesInput, newLocationsInput, destinationFolder);
    }

}
