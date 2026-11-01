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
package esa.mo.nmf.accesscontrol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.accesscontrol.MALAccessControl;
import org.ccsds.moims.mo.mal.accesscontrol.MALAccessControlFactory;
import org.ccsds.moims.mo.mal.accesscontrol.MALCheckErrorException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * The access control of an NMF process, which the MAL calls for every message
 * that the process sends or receives. It plays one of two roles:
 * <ul>
 * <li>In an App or a ground tool, it presents the process's credential on the
 * requests it sends to the Supervisor, and to nothing else, so that no other
 * App can read it.</li>
 * <li>In the Supervisor, it has the {@link AccessControlAuthority} check every
 * request that the Supervisor receives.</li>
 * </ul>
 * The Supervisor never presents a credential: the MAL calls the same check for
 * the messages a process sends and for those it receives, so a credential
 * presented there would also be put on the requests that arrive without one.
 * It only marks what its own publishers send to its own brokers, telling the
 * messages it sends from those it receives by who calls the check.
 */
public final class NMFAccessControl implements MALAccessControl {

    /** The access control of this process. */
    public static final NMFAccessControl INSTANCE = new NMFAccessControl();

    /**
     * The system property, set for an App by the Supervisor, that holds the
     * path of the file with the App's credential.
     */
    public static final String CREDENTIAL_FILE_PROPERTY = "esa.mo.nmf.accesscontrol.credentialFile";

    /**
     * The system property that a ground tool, such as the CTT or the CLI of the
     * playground, uses to find the credential of ground.
     */
    public static final String GROUND_CREDENTIAL_FILE_PROPERTY = "esa.mo.nmf.accesscontrol.groundCredentialFile";

    /**
     * The system property, set for an App by the Supervisor, that holds the
     * permissions the App was granted, separated by {@code ;}.
     */
    public static final String GRANTED_PROPERTY = "esa.mo.nmf.accesscontrol.granted";

    private static final Logger LOGGER = Logger.getLogger(NMFAccessControl.class.getName());

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The classes of the MAL implementation that call the check for the messages
     * that the process sends: requests and replies, and what its brokers send on.
     * The receiving side calls it from another class.
     */
    private static final Set<String> MAL_SENDERS = Set.of(
            "esa.mo.mal.impl.MALSender", "esa.mo.mal.impl.broker.MALBrokerBindingImpl");

    private static final StackWalker STACK = StackWalker.getInstance();

    private volatile byte[] credential;
    private volatile String credentialTarget;
    private volatile Set<String> granted = Collections.emptySet();
    private volatile AccessControlAuthority authority;

    /** Whether the credential is ground's, which signs over the Supervisor's run id. */
    private volatile boolean ground;
    /** The run id that the Supervisor last gave, or null before it gave one. */
    private volatile byte[] runId;
    /** Whether the Supervisor has answered without a run id: it takes credentials as they are. */
    private volatile boolean answeredPlain;

    private final AtomicLong counter = new AtomicLong();
    private final ThreadLocal<byte[]> session = ThreadLocal.withInitial(NMFAccessControl::newSession);

    private NMFAccessControl() {
    }

    /**
     * Makes the MAL use this access control in every MAL context that this
     * process creates from now on. It must be called before the process opens
     * its first connection.
     */
    public static void install() {
        if (System.getProperty(MALAccessControlFactory.FACTORY_PROP_NAME) == null) {
            System.setProperty(MALAccessControlFactory.FACTORY_PROP_NAME,
                    NMFAccessControlFactory.class.getName());
        }
    }

    /**
     * Presents the given credential on the requests that this process sends to
     * the Supervisor at the given URI, and only there.
     *
     * @param secret the credential
     * @param supervisorURI any URI of the Supervisor, such as its Directory's
     */
    public void presentCredential(final byte[] secret, final String supervisorURI) {
        present(secret, supervisorURI, false);
    }

    /**
     * Presents the credential of ground on the requests that this tool sends to
     * the Supervisor at the given URI, and only there. Under signed credentials
     * the tool signs over the run id the Supervisor gives it, as a ground tool
     * does; it is the credential, not the way it is presented, that may call
     * every service.
     *
     * @param secret the ground credential
     * @param supervisorURI any URI of the Supervisor, such as its Directory's
     */
    public void presentGroundCredential(final byte[] secret, final String supervisorURI) {
        present(secret, supervisorURI, true);
    }

    private synchronized void present(final byte[] secret, final String supervisorURI, final boolean isGround) {
        this.credentialTarget = addressOf(supervisorURI);
        this.credential = secret.clone();
        this.ground = isGround;
        this.runId = null;
        this.answeredPlain = false;
    }

    /**
     * Presents the credential of ground, from the file that the
     * {@value #GROUND_CREDENTIAL_FILE_PROPERTY} system property names, on the
     * requests that this ground tool sends to the Supervisor whose Directory is
     * at the given URI, and only there, so that no App receives it.
     *
     * @param directoryURI the URI of the Supervisor's Directory that the tool uses
     * @return true if a credential is now presented, false if none is configured
     */
    public static boolean presentGroundCredential(final String directoryURI) {
        String file = System.getProperty(GROUND_CREDENTIAL_FILE_PROPERTY);
        if (file == null || directoryURI == null) {
            return false;
        }
        install();
        try {
            INSTANCE.present(readCredential(file), directoryURI, true);
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "The ground credential could not be read from " + file, ex);
            return false;
        }
    }

    /**
     * Sets the permissions that this App was granted, so that it can warn about
     * a call to a service it lacks before the Supervisor answers.
     *
     * @param permissions the granted permissions
     */
    public void setGranted(final Collection<String> permissions) {
        this.granted = Collections.unmodifiableSet(new HashSet<>(permissions));
    }

    /**
     * Has the given authority check every request that this process receives:
     * only the Supervisor does this.
     *
     * @param authority the authority of the Supervisor
     */
    public void enforce(final AccessControlAuthority authority) {
        this.authority = authority;
    }

    /**
     * Returns the authority that checks the requests this process receives, or
     * null if this process is not the Supervisor.
     *
     * @return the authority, or null
     */
    public AccessControlAuthority getAuthority() {
        return authority;
    }

    @Override
    public MALMessage check(final MALMessage msg) throws IllegalArgumentException, MALCheckErrorException {
        MALMessageHeader header = msg.getHeader();
        AccessControlAuthority a = authority;
        if (a != null) {
            // What the Supervisor sends is not checked: only what it receives. The
            // destination in a received header cannot tell, as the sender writes it.
            if (isSentByThisProcess()) {
                a.markOwnMessage(header);
            } else {
                a.check(header);
            }
            return msg;
        }

        byte[] c = credential;
        String target = credentialTarget;
        if (c == null || target == null) {
            return msg;
        }
        if (!AccessControlAuthority.isInitiating(header)) {
            // An answer from the Supervisor says whether it wants signed proofs
            if (target.equals(addressOf(header.getFromURI()))) {
                learnFromAnswer(header);
            }
            return msg;
        }

        // A request to the Supervisor proves this process's credential
        if (target.equals(addressOf(header.getToURI()))) {
            if (isEmpty(header.getAuthenticationId())) {
                Blob proof = proofFor(c, header);
                if (proof != null) {
                    header.setAuthenticationId(proof);
                }
            }
            warnIfNotGranted(header);
        }
        return msg;
    }

    /**
     * Notes what an answer of the Supervisor says: a run id to sign over, or,
     * when it carries none and none came before, that the Supervisor takes
     * credentials as they are. Once a run id has come, a ground tool never sends
     * its credential as it is again.
     */
    private void learnFromAnswer(final MALMessageHeader header) {
        Blob id = header.getAuthenticationId();
        if (!isEmpty(id) && id.getValue().length == CredentialProof.RUN_ID_LENGTH) {
            runId = id.getValue().clone();
        } else if (runId == null) {
            answeredPlain = true;
        }
    }

    /**
     * Returns what a request to the Supervisor carries to prove this process's
     * credential, or null for nothing yet.
     * <ul>
     * <li>An App follows the mission's {@link CredentialsMode}, which the
     * Supervisor passes to it.</li>
     * <li>A ground tool follows the Supervisor: it signs over the run id the
     * Supervisor gave, sends the credential as it is to a Supervisor that
     * answered without one, and sends nothing before the first answer.</li>
     * </ul>
     */
    private Blob proofFor(final byte[] secret, final MALMessageHeader header) {
        if (ground) {
            byte[] id = runId;
            if (id != null) {
                return new Blob(CredentialProof.sign(secret, session.get(), counter.incrementAndGet(), id, header));
            }
            return answeredPlain ? new Blob(secret.clone()) : null;
        }
        if (CredentialsMode.current() == CredentialsMode.PLAIN) {
            return new Blob(secret.clone());
        }
        return new Blob(CredentialProof.sign(secret, session.get(), counter.incrementAndGet(), null, header));
    }

    /**
     * Returns whether the MAL checks the current message because this process
     * sends it, rather than because it received it. A call from anywhere else
     * counts as received, so that it is checked.
     */
    private static boolean isSentByThisProcess() {
        String caller = STACK.walk(frames -> frames.map(StackWalker.StackFrame::getClassName)
                .filter(name -> !name.equals(NMFAccessControl.class.getName()))
                .findFirst().orElse(""));
        return MAL_SENDERS.contains(caller);
    }

    /**
     * Logs, in the App, a call to a service of the Supervisor that the App was
     * not granted: in audit mode it goes through, in enforce mode it is refused.
     */
    private void warnIfNotGranted(final MALMessageHeader header) {
        AccessControlMode mode = AccessControlMode.current();
        if (mode == AccessControlMode.OFF) {
            return;
        }
        AccessControlAuthority.Call call = AccessControlAuthority.Call.of(header);
        if (call == null || call.isLookup() || call.isBaseline() || granted.contains(call.permission)) {
            return;
        }
        LOGGER.log(Level.WARNING, "{0}: {1} The call to {2} ({3}) {4}", new Object[]{
            mode == AccessControlMode.AUDIT ? "Audit" : "Refused",
            AccessControlAuthority.missingPermission(call.permission), call.permission, call.operation,
            mode == AccessControlMode.AUDIT ? "goes through in audit mode; enforce mode would refuse it."
            : "is refused by the Supervisor."});
    }

    /**
     * Returns a new random credential.
     *
     * @return 32 random bytes
     */
    public static byte[] newCredential() {
        byte[] secret = new byte[32];
        RANDOM.nextBytes(secret);
        return secret;
    }

    /**
     * Returns a new random session identifier for a {@link CredentialProof}.
     *
     * @return {@value CredentialProof#SESSION_LENGTH} random bytes
     */
    static byte[] newSession() {
        byte[] id = new byte[CredentialProof.SESSION_LENGTH];
        RANDOM.nextBytes(id);
        return id;
    }

    /**
     * Returns a credential as text, to be written into a file.
     *
     * @param secret the credential
     * @return 64 lowercase hexadecimal digits
     */
    public static String toText(final byte[] secret) {
        StringBuilder text = new StringBuilder();
        for (byte b : secret) {
            text.append(String.format("%02x", b));
        }
        return text.toString();
    }

    /**
     * Reads a credential from a file written by {@link #toText}.
     *
     * @param path the file
     * @return the credential
     * @throws IOException if the file cannot be read or holds no credential
     */
    public static byte[] readCredential(final String path) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.US_ASCII).trim();
        if (!text.matches("([0-9a-f]{2})+")) {
            throw new IOException("The file holds no credential: " + path);
        }
        byte[] secret = new byte[text.length() / 2];
        for (int i = 0; i < secret.length; i++) {
            secret[i] = (byte) Integer.parseInt(text.substring(2 * i, 2 * i + 2), 16);
        }
        return secret;
    }

    /** Returns the transport address of a URI: its scheme, host and port. */
    static String addressOf(final Object uri) {
        if (uri == null) {
            return null;
        }
        String text = (uri instanceof URI) ? ((URI) uri).getValue() : uri.toString();
        int scheme = text.indexOf("://");
        int path = text.indexOf('/', scheme < 0 ? 0 : scheme + 3);
        return path < 0 ? text : text.substring(0, path);
    }

    static boolean isEmpty(final Blob blob) {
        return blob == null || blob.getValue() == null || blob.getValue().length == 0;
    }
}
