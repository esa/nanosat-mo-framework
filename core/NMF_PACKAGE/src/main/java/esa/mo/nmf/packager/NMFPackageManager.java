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
package esa.mo.nmf.packager;

/**
 *
 * @author Cesar Coelho
 */
public class NMFPackageManager {

    public static void install(final String packageLocation) {
        // Get the File to be installed

        // Unpack the package to a temporary folder

        // Copy the files according to the NMF statement file
        
        // Delete the temporary folder
        
    }
    
    public static void uninstall(final String packageLocation, final boolean keepConfigurations) {
        // Get the Files to be installed


        // Delete the files according to the NMF statement file
        
        // Do we keep the previous configurations?

    }

    
    public static void upgrade(final String packageLocation) {
        // Get the Files to be installed

        // Upgrade the files according to the NMF statement file
        // Keep the same configurations
        
        
    }
    

}
