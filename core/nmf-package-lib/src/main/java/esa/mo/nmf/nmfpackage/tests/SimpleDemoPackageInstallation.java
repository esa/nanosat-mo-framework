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
package esa.mo.nmf.nmfpackage.tests;

import esa.mo.nmf.nmfpackage.NMFPackageManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class SimpleDemoPackageInstallation {

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        SimpleDemoPackageInstallation.installPackage();
    }
    
    
    public static void installPackage(){
        String filename = "C:\\Users\\Cesar Coelho\\Dropbox\\PhD\\NetBeansProjects\\AGSA-LAB\\NMF\\NMF_CORE\\NMF_PACKAGE\\TestPackage-1.0.nmfpack";
                
        try {
            NMFPackageManager.install(filename);
        } catch (IOException ex) {
            Logger.getLogger(SimpleDemoPackageInstallation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    


}
