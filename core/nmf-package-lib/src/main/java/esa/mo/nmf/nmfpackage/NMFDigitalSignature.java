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
package esa.mo.nmf.nmfpackage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class NMFDigitalSignature {

    private static final String SIGNATURE_ALGORITHM = "SHA1withDSA";
    private static final String SIGNATURE_PROVIDER = "SUN";

    public static KeyPair generateKeyPar() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            final SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

            return keyGen.generateKeyPair();
        } catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(NMFDigitalSignature.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static byte[] signWithData(final PrivateKey privateKey, final String file) {
        try {
            final Signature dsa = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            dsa.initSign(privateKey);

            final FileInputStream fis = new FileInputStream(file);
            final BufferedInputStream bufin = new BufferedInputStream(fis);
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = bufin.read(buffer)) >= 0) {
                dsa.update(buffer, 0, len);
            }
            bufin.close();

            return dsa.sign(); // Returns the signature
        } catch (final NoSuchProviderException | SignatureException | InvalidKeyException | NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(NMFDigitalSignature.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Verify a signature.
     *
     * @param publicKey The public key
     * @param signatureToVerify The signature to verify
     * @param file The signed file
     * @return
     */
    public static boolean verifyDigitalSignature(final PublicKey publicKey, final byte[] signatureToVerify, final String file) {
        // https://docs.oracle.com/javase/tutorial/security/apisign/vstep4.html
        try {
            final Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            sig.initVerify(publicKey);

            final FileInputStream datafis = new FileInputStream(file);
            final BufferedInputStream bufin = new BufferedInputStream(datafis);

            final byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                sig.update(buffer, 0, len);
            }

            bufin.close();

            return sig.verify(signatureToVerify);
        } catch (final NoSuchProviderException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | IOException ex) {
            Logger.getLogger(NMFDigitalSignature.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

}
