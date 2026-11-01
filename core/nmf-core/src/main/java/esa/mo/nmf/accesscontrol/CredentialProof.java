/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.accesscontrol;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * The proof that a caller puts on a request so that the Supervisor knows whose
 * credential it comes from, without the credential ever being sent.
 * <p>
 * The caller signs the request with its credential, over a counter that only
 * grows, and the Supervisor checks the signature with the same credential and
 * refuses a counter it has already accepted (see {@link CounterWindow}). A
 * request read off the wire therefore cannot be sent again, nor can what proves
 * it be moved to a request for another operation, because the signature also
 * covers which service and operation the request is for.
 * <p>
 * The proof is a token of {@value #LENGTH} bytes carried in the
 * {@code authenticationId} of the header:
 * <pre>
 *   1 byte   version, {@value #VERSION}
 *   4 bytes  key id: the first bytes of the SHA-256 of the credential, which
 *            names the credential without revealing it
 *   8 bytes  session: chosen at random by the caller's process
 *   8 bytes  counter: raised by the caller with each request
 *  16 bytes  the first bytes of the HMAC-SHA256, keyed with the credential, of
 *            all of the above and of the run id and the request's routing
 * </pre>
 */
public final class CredentialProof {

    /** The version of the token layout, so that it can change later. */
    public static final byte VERSION = 1;

    /** The length of the key id: enough to tell the running credentials apart. */
    public static final int KEY_ID_LENGTH = 4;

    /** The length of the session identifier. */
    public static final int SESSION_LENGTH = 8;

    /** The length of the truncated HMAC, 128 bits, which is beyond brute force. */
    public static final int MAC_LENGTH = 16;

    /** The length of the run id that a ground tool signs over; see {@link AccessControlAuthority}. */
    public static final int RUN_ID_LENGTH = 16;

    private static final int COUNTER_LENGTH = 8;

    /** The total length of the token. */
    public static final int LENGTH = 1 + KEY_ID_LENGTH + SESSION_LENGTH + COUNTER_LENGTH + MAC_LENGTH;

    private static final String HMAC = "HmacSHA256";
    private static final String DIGEST = "SHA-256";

    private CredentialProof() {
    }

    /**
     * Returns the key id of a credential: the first {@value #KEY_ID_LENGTH}
     * bytes of its SHA-256 digest. It names the credential on the wire without
     * revealing it, as the digest cannot be turned back into the credential.
     *
     * @param credential the credential
     * @return the key id
     */
    public static byte[] keyId(final byte[] credential) {
        try {
            byte[] digest = MessageDigest.getInstance(DIGEST).digest(credential);
            byte[] id = new byte[KEY_ID_LENGTH];
            System.arraycopy(digest, 0, id, 0, KEY_ID_LENGTH);
            return id;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(DIGEST + " is required but missing", e);
        }
    }

    /**
     * Builds the token for a request.
     *
     * @param credential the caller's credential
     * @param session the caller's session identifier, {@value #SESSION_LENGTH} bytes
     * @param counter the counter of this request
     * @param runId the Supervisor's run id a ground tool signs over, or null for an App
     * @param header the header of the request
     * @return the token, {@value #LENGTH} bytes
     */
    public static byte[] sign(final byte[] credential, final byte[] session, final long counter,
            final byte[] runId, final MALMessageHeader header) {
        byte[] keyId = keyId(credential);
        byte[] mac = mac(credential, keyId, session, counter, runId, header);
        ByteBuffer token = ByteBuffer.allocate(LENGTH);
        token.put(VERSION);
        token.put(keyId);
        token.put(session);
        token.putLong(counter);
        token.put(mac, 0, MAC_LENGTH);
        return token.array();
    }

    /**
     * Returns whether the token on a request was made with the given
     * credential, for this request and this run id. It does not look at the
     * counter; the {@link CounterWindow} does that.
     *
     * @param credential the credential to check against
     * @param token the token from the request's {@code authenticationId}
     * @param runId the Supervisor's run id, or null if none is required
     * @param header the header of the request
     * @return true if the token is genuine
     */
    public static boolean verify(final byte[] credential, final byte[] token, final byte[] runId,
            final MALMessageHeader header) {
        Parsed parsed = parse(token);
        if (parsed == null) {
            return false;
        }
        byte[] expected = mac(credential, parsed.keyId, parsed.session, parsed.counter, runId, header);
        byte[] truncated = new byte[MAC_LENGTH];
        System.arraycopy(expected, 0, truncated, 0, MAC_LENGTH);
        return MessageDigest.isEqual(truncated, parsed.mac);
    }

    /**
     * Reads a token, or returns null if the bytes are not a token of a version
     * this code knows.
     *
     * @param token the bytes
     * @return the parsed token, or null
     */
    public static Parsed parse(final byte[] token) {
        if (token == null || token.length != LENGTH || token[0] != VERSION) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(token);
        buffer.get(); // version
        byte[] keyId = new byte[KEY_ID_LENGTH];
        buffer.get(keyId);
        byte[] session = new byte[SESSION_LENGTH];
        buffer.get(session);
        long counter = buffer.getLong();
        byte[] mac = new byte[MAC_LENGTH];
        buffer.get(mac);
        return new Parsed(keyId, session, counter, mac);
    }

    /**
     * Returns whether the bytes look like a token rather than a credential sent
     * as it is, so that the two can be told apart in the one
     * {@code authenticationId} field.
     *
     * @param authenticationId the bytes
     * @return true if they are a token
     */
    public static boolean looksLikeToken(final byte[] authenticationId) {
        return authenticationId != null && authenticationId.length == LENGTH
                && authenticationId[0] == VERSION;
    }

    private static byte[] mac(final byte[] credential, final byte[] keyId, final byte[] session,
            final long counter, final byte[] runId, final MALMessageHeader header) {
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(credential, HMAC));
            ByteBuffer signed = ByteBuffer.allocate(1 + KEY_ID_LENGTH + SESSION_LENGTH + COUNTER_LENGTH
                    + RUN_ID_LENGTH + 2 + 1 + 2 + 2 + 1 + 1);
            signed.put(VERSION);
            signed.put(keyId);
            signed.put(session);
            signed.putLong(counter);
            signed.put(runId == null ? new byte[RUN_ID_LENGTH] : runId);
            signed.putShort(value(header.getServiceArea()));
            signed.put(octet(header.getAreaVersion()));
            signed.putShort(value(header.getService()));
            signed.putShort(value(header.getOperation()));
            signed.put((byte) (header.getInteractionType() == null ? -1 : header.getInteractionType().getValue()));
            signed.put(octet(header.getInteractionStage()));
            return mac.doFinal(signed.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(HMAC + " is required but missing", e);
        }
    }

    private static short value(final UShort field) {
        return field == null ? 0 : (short) field.getValue();
    }

    private static byte octet(final UOctet field) {
        return field == null ? (byte) -1 : (byte) field.getValue();
    }

    /** A token read back into its parts. */
    public static final class Parsed {

        final byte[] keyId;
        final byte[] session;
        final long counter;
        final byte[] mac;

        private Parsed(byte[] keyId, byte[] session, long counter, byte[] mac) {
            this.keyId = keyId;
            this.session = session;
            this.counter = counter;
            this.mac = mac;
        }
    }
}
