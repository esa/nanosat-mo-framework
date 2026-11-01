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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.AuthenticationFailedException;
import org.ccsds.moims.mo.mal.AuthorisationFailException;
import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.accesscontrol.MALCheckErrorException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * The Supervisor's authority over who may call its services: the credential of
 * each running App with the permissions it was granted, and the credential of
 * ground, which may call everything.
 * <p>
 * Every App may use the baseline without declaring it: the Directory, the
 * Heartbeat, and the events of the Apps Launcher, which carry its shutdown
 * signal. A caller without a valid credential may only look up the Directory.
 * <p>
 * The entries of the Directory belong to their providers: an App may add or
 * remove only its own entry, and no caller, ground included, may add or remove
 * the Supervisor's (see {@link #checkDirectoryEntry}).
 * <p>
 * Only the Supervisor itself publishes on its services: it marks what its own
 * publishers send to its brokers, and the brokers refuse every other publisher.
 * It marks the requests that it sends to its own services in the same way, so
 * that they are accepted without a credential of their own.
 * <p>
 * Under {@link CredentialsMode#SIGNED}, the default, a caller proves its
 * credential with a {@link CredentialProof} instead of sending it, and the
 * authority refuses a proof whose counter it has already accepted, so that a
 * request read off the wire cannot be sent again. A ground tool also signs over
 * the run id of this Supervisor, which the Supervisor gives on its replies, so
 * that what was read in one run is refused in the next.
 */
public final class AccessControlAuthority {

    private static final Logger LOGGER = Logger.getLogger(AccessControlAuthority.class.getName());

    /**
     * How many ground sessions one run id serves before the Supervisor chooses a
     * new one. The sessions of the run id before the previous are then dropped.
     */
    static final int MAX_GROUND_SESSIONS = 10_000;

    /**
     * How many sessions an App, or the Supervisor itself, may have. A process
     * signs with one session, and an App gets a new credential each time it
     * starts, so this only bounds what a misbehaving caller can make the
     * Supervisor hold.
     */
    private static final int MAX_SESSIONS = 64;

    /** A known caller: an App with what it was granted, or ground. */
    private static final class Caller {

        private final String app;
        private final Set<String> granted;
        private final byte[] secret;
        private final byte[] keyId;
        /** The counters each session of an App has used; ground keeps its sessions per run id. */
        private final Map<String, CounterWindow> sessions = new ConcurrentHashMap<>();

        Caller(String app, Collection<String> granted, byte[] secret) {
            this.app = app;
            this.granted = Collections.unmodifiableSet(new HashSet<>(granted));
            this.secret = secret.clone();
            this.keyId = CredentialProof.keyId(secret);
        }

        boolean isGround() {
            return app == null;
        }
    }

    /** The ground sessions signed over one run id of the Supervisor. */
    private static final class Generation {

        private final byte[] runId;
        private final Map<String, CounterWindow> sessions = new ConcurrentHashMap<>();

        Generation() {
            this.runId = Arrays.copyOf(NMFAccessControl.newCredential(), CredentialProof.RUN_ID_LENGTH);
        }
    }

    /** The Supervisor itself, as a caller of its own services. */
    private final Caller self;

    /**
     * Records each call made without the permission or credential it needs, or
     * that adds or removes an entry of the Directory that belongs to another
     * provider.
     */
    public interface Recorder {

        /**
         * Records a call made without the permission or credential it needs, or
         * that adds or removes an entry of the Directory that belongs to another
         * provider.
         *
         * @param app the App that made it, or null if the caller had no valid credential
         * @param permission the permission it needs
         * @param operation the operation called
         * @param from the URI of the caller
         * @param entry the name of the provider whose entry of the Directory the call
         * tried to add or remove, or null for a call that lacks a permission or credential
         * @param refused true if it was refused, false if it was only audited
         */
        void record(String app, String permission, String operation, URI from, String entry, boolean refused);
    }

    private final Set<String> supervisorAddresses = new HashSet<>();
    private volatile Recorder recorder;
    private final Map<String, Caller> callers = new ConcurrentHashMap<>();
    private final Map<String, String> credentialOfApp = new ConcurrentHashMap<>();
    /**
     * The credential with which the Supervisor marks the messages it sends to
     * its own services. It is never given out; under signed credentials it does
     * not leave the Supervisor either, only proofs made with it do.
     */
    private final byte[] ownMark = NMFAccessControl.newCredential();
    private final String ownKey = NMFAccessControl.toText(ownMark);
    private final AtomicLong ownCounter = new AtomicLong();
    private final byte[] ownSession = NMFAccessControl.newSession();

    private volatile Generation current = new Generation();
    private volatile Generation previous;

    /**
     * Creates the authority of a Supervisor.
     *
     * @param supervisorURIs a URI of the Supervisor's own services on each of
     * its transports; null ones are skipped. Only the Supervisor's own
     * publishing to these addresses is marked.
     */
    public AccessControlAuthority(final String... supervisorURIs) {
        for (String uri : supervisorURIs) {
            if (uri != null) {
                supervisorAddresses.add(NMFAccessControl.addressOf(uri));
            }
        }
        self = new Caller(null, Collections.emptySet(), ownMark);
    }

    /**
     * Sets what records each call made without the permission or credential it
     * needs, such as in the Archive.
     *
     * @param recorder the recorder
     */
    public void setRecorder(final Recorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Sets the credential of ground, which may call every service.
     *
     * @param secret the ground credential
     */
    public void setGroundCredential(final byte[] secret) {
        callers.put(NMFAccessControl.toText(secret), new Caller(null, Collections.emptySet(), secret));
    }

    /**
     * Registers the credential of an App for as long as it runs, replacing any
     * earlier one.
     *
     * @param app the name of the App
     * @param secret its credential
     * @param granted the permissions it was granted at installation
     */
    public void registerApp(final String app, final byte[] secret, final Collection<String> granted) {
        revokeApp(app);
        String key = NMFAccessControl.toText(secret);
        callers.put(key, new Caller(app, granted, secret));
        credentialOfApp.put(app, key);
    }

    /**
     * Revokes the credential of an App, once it has stopped.
     *
     * @param app the name of the App
     */
    public void revokeApp(final String app) {
        String key = credentialOfApp.remove(app);
        if (key != null) {
            callers.remove(key);
        }
    }

    /**
     * Checks a message that the Supervisor received, whatever its destination
     * says: the sender writes the destination, and the transport delivers by the
     * service name at its end alone. A refused message throws, so that the MAL
     * returns the error to the caller instead of delivering it.
     *
     * @param header the header of the message
     * @throws MALCheckErrorException if the message is refused
     */
    public void check(final MALMessageHeader header) throws MALCheckErrorException {
        AccessControlMode mode = AccessControlMode.current();
        if (mode == AccessControlMode.OFF) {
            return;
        }
        if (isPublishing(header)) {
            checkPublishing(mode, header);
            return;
        }
        if (!isInitiating(header)) {
            return;
        }
        Caller caller = identify(header, true);
        if (caller == self) {
            return; // The Supervisor's own request to one of its services
        }
        if (caller != null && caller.isGround()) {
            return;
        }
        Call call = Call.of(header);
        if (call == null) {
            // A service, operation or area version that the Supervisor does not know: the
            // MAL finds the provider by the area and service numbers alone, so it is refused
            call = Call.unknown(header);
            refuse(mode, new AuthorisationFailException("Unknown service: " + call.service),
                    "a call to the unknown service " + call.service + " (" + call.operation + ") from "
                    + header.getFromURI(), caller == null ? null : caller.app, call, header);
            return;
        }
        if (call.isLookup()) {
            return; // Anyone may look up the services
        }
        if (caller == null) {
            refuse(mode, new AuthenticationFailedException("No valid credentials"),
                    "a call to " + call.permission + " (" + call.operation + ") from " + header.getFromURI()
                    + " carries no valid credentials", null, call, header);
            return;
        }
        if (call.isBaseline() || caller.granted.contains(call.permission)) {
            return;
        }
        refuse(mode, new AuthorisationFailException(missingPermission(call.permission)),
                "App '" + caller.app + "' called " + call.permission + " (" + call.operation
                + ") without the permission " + call.permission, caller.app, call, header);
    }

    /**
     * Checks that the caller of a request to the Supervisor's Directory may add
     * or remove the entry of the given provider. Only an App may, and only its
     * own entry, which it adds when it starts and removes when it stops: ground
     * and the other Apps may not, so that no caller can make a look-up of an
     * App, or of the Supervisor, lead elsewhere. The entry that the Supervisor
     * added for itself is refused to every caller in every mode, so that a
     * look-up of the Supervisor always leads to the Supervisor.
     *
     * @param header the header of the request
     * @param entry the name of the provider whose entry the request adds or removes
     * @param operation the operation: add or remove
     * @param supervisorsOwn true if the Supervisor added the entry for itself
     * @throws AuthorisationFailException if the request is refused
     */
    public void checkDirectoryEntry(final MALMessageHeader header, final String entry,
            final String operation, final boolean supervisorsOwn) throws AuthorisationFailException {
        AccessControlMode mode = AccessControlMode.current();
        // The request was checked, and its counter used, when it arrived
        Caller caller = identify(header, false);
        if (caller == self) {
            caller = null;
        }
        String app = (caller == null) ? null : caller.app;
        String what;

        if (supervisorsOwn) {
            what = callerName(caller, header) + " tried to " + operation + " the entry of '" + entry
                    + "' in the Directory, which the Supervisor added for itself";
            mode = AccessControlMode.ENFORCE; // Refused whatever the mode
        } else if (mode == AccessControlMode.OFF || (app != null && app.equals(entry))) {
            return;
        } else {
            what = callerName(caller, header) + " tried to " + operation + " the entry of '" + entry
                    + "' in the Directory: only the App '" + entry + "' may";
        }

        Call call = new Call("COM.Directory", operation, false);
        if (record(mode, what, app, call, header, entry)) {
            throw new AuthorisationFailException("Only the provider '" + entry
                    + "' may " + operation + " its entry in the Directory");
        }
    }

    private static String callerName(final Caller caller, final MALMessageHeader header) {
        if (caller == null) {
            return "a caller at " + header.getFromURI() + " without valid credentials";
        }
        return caller.isGround() ? "ground" : "App '" + caller.app + "'";
    }

    /**
     * Marks a message that the Supervisor sends to one of its own services, so
     * that the service accepts it: what one of its publishers sends to one of
     * its brokers, and a request of one of its consumers, such as the one with
     * which the Supervisor monitors its own attitude. The NMF access control
     * calls this only for the messages that the Supervisor sends; the mark is
     * not given out, so no other process can make it.
     *
     * <p>
     * Under signed credentials, the replies of the Directory and the errors that
     * the Supervisor sends to anyone else carry its current run id, so that a
     * ground tool learns what to sign over from the look-up of the Directory it
     * makes first, which needs no credential (see {@link #carriesRunId}).
     *
     * @param header the header of the message that the Supervisor sends
     */
    public void markOwnMessage(final MALMessageHeader header) {
        boolean signed = CredentialsMode.current() == CredentialsMode.SIGNED;
        if (!isToSupervisor(header)) {
            if (signed && carriesRunId(header) && NMFAccessControl.isEmpty(header.getAuthenticationId())) {
                header.setAuthenticationId(new Blob(current.runId.clone()));
            }
            return;
        }
        if (isPublishing(header)
                || (isInitiating(header) && NMFAccessControl.isEmpty(header.getAuthenticationId()))) {
            header.setAuthenticationId(new Blob(signed
                    ? CredentialProof.sign(ownMark, ownSession, ownCounter.incrementAndGet(), null, header)
                    : ownMark.clone()));
        }
    }

    /**
     * Returns whether a message that the Supervisor sends to someone else is one
     * that gives the run id: a reply of the Directory, which a ground tool gets
     * first, from the look-up that needs no credential, and an error, which a
     * tool gets when its run id is no longer accepted. Other replies and the
     * notifications go without it.
     */
    private static boolean carriesRunId(final MALMessageHeader header) {
        if (isInitiating(header) || isPublishing(header)) {
            return false;
        }
        if (Boolean.TRUE.equals(header.getIsErrorMessage())) {
            return true;
        }
        Call call = Call.of(header);
        return call != null && "COM.Directory".equals(call.service);
    }

    /**
     * Returns the run id that a ground tool signs over now.
     *
     * @return a copy of the current run id
     */
    byte[] currentRunId() {
        return current.runId.clone();
    }

    /**
     * Finds the caller whose credential a message carries or proves.
     * <p>
     * Under plain credentials the message must carry a credential as it is.
     * Under signed credentials it must carry a {@link CredentialProof} made with
     * a credential this authority knows, for this message, and, when the
     * counter is consumed, with a counter that its session has not used yet; a
     * credential sent as it is does not count.
     *
     * @param header the header of the message
     * @param consume true to use up the proof's counter, false when the message
     * was already checked on arrival
     * @return the caller, {@link #self} for the Supervisor's own messages, or
     * null if the message proves no known credential
     */
    private Caller identify(final MALMessageHeader header, final boolean consume) {
        Blob blob = header.getAuthenticationId();
        if (NMFAccessControl.isEmpty(blob)) {
            return null;
        }
        byte[] value = blob.getValue();
        if (CredentialsMode.current() == CredentialsMode.PLAIN) {
            String key = NMFAccessControl.toText(value);
            return ownKey.equals(key) ? self : callers.get(key);
        }

        CredentialProof.Parsed proof = CredentialProof.parse(value);
        if (proof == null) {
            return null; // A credential sent as it is, or nothing this authority knows
        }
        if (Arrays.equals(proof.keyId, self.keyId) && CredentialProof.verify(ownMark, value, null, header)) {
            return fresh(self.sessions, proof, consume, MAX_SESSIONS) ? self : null;
        }
        for (Caller caller : callers.values()) {
            if (!Arrays.equals(proof.keyId, caller.keyId)) {
                continue;
            }
            if (caller.isGround()) {
                if (groundProofHolds(caller, value, proof, header, consume)) {
                    return caller;
                }
            } else if (CredentialProof.verify(caller.secret, value, null, header)
                    && fresh(caller.sessions, proof, consume, MAX_SESSIONS)) {
                return caller;
            }
        }
        return null;
    }

    /**
     * Checks a ground proof against the current run id, and then against the
     * previous one, whose sessions are kept until the next change of run id.
     * When the current run id has served {@value #MAX_GROUND_SESSIONS} sessions,
     * a new session makes the Supervisor choose a new run id and is refused; the
     * error that refuses it carries the new run id, so the tool's next request
     * is accepted.
     */
    private boolean groundProofHolds(final Caller ground, final byte[] value, final CredentialProof.Parsed proof,
            final MALMessageHeader header, final boolean consume) {
        Generation now = current;
        if (CredentialProof.verify(ground.secret, value, now.runId, header)) {
            if (!consume) {
                return true;
            }
            if (!now.sessions.containsKey(sessionOf(proof)) && now.sessions.size() >= MAX_GROUND_SESSIONS) {
                newRunId(now);
                return false;
            }
            return fresh(now.sessions, proof, true, Integer.MAX_VALUE);
        }
        Generation before = previous;
        if (before != null && CredentialProof.verify(ground.secret, value, before.runId, header)) {
            // Only the sessions that the previous run id already had
            return !consume || (before.sessions.containsKey(sessionOf(proof))
                    && fresh(before.sessions, proof, true, Integer.MAX_VALUE));
        }
        return false;
    }

    private synchronized void newRunId(final Generation full) {
        if (current == full) {
            previous = full;
            current = new Generation();
            LOGGER.log(Level.INFO, "The ground sessions of this run id reached {0}: a new run id is in use",
                    MAX_GROUND_SESSIONS);
        }
    }

    /**
     * Uses up the counter of a proof in its session, starting the session if it
     * is new and there is room for it.
     *
     * @return true if the counter had not been used, or if nothing is consumed
     */
    private static boolean fresh(final Map<String, CounterWindow> sessions, final CredentialProof.Parsed proof,
            final boolean consume, final int maxSessions) {
        if (!consume) {
            return true;
        }
        String session = sessionOf(proof);
        CounterWindow window = sessions.get(session);
        if (window == null) {
            if (sessions.size() >= maxSessions) {
                return false;
            }
            window = sessions.computeIfAbsent(session, s -> new CounterWindow());
        }
        synchronized (window) {
            return window.accept(proof.counter);
        }
    }

    private static String sessionOf(final CredentialProof.Parsed proof) {
        return NMFAccessControl.toText(proof.session);
    }

    /**
     * Refuses a publisher's message to a broker of the Supervisor, unless one of
     * the Supervisor's own publishers sent it.
     */
    private void checkPublishing(AccessControlMode mode, MALMessageHeader header) throws MALCheckErrorException {
        if (identify(header, true) == self) {
            return;
        }
        Call call = Call.of(header);
        String stage = stageName(header.getInteractionStage().getValue());
        if (call == null) {
            call = new Call("?", stage, true); // Not a service of a known area: refused all the same
        } else {
            call = new Call(call.service, call.operation + " " + stage, true);
        }
        refuse(mode, new AuthorisationFailException("Only the Supervisor publishes on its own services"),
                "a publisher at " + header.getFromURI() + " sent a " + stage + " on " + call.permission
                + ": only the Supervisor publishes on its own services", null, call, header);
    }

    private static String stageName(final short stage) {
        switch (stage) {
            case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
                return "PUBLISH_REGISTER";
            case MALPubSubOperation._PUBLISH_STAGE:
                return "PUBLISH";
            default:
                return "PUBLISH_DEREGISTER";
        }
    }

    private boolean isToSupervisor(final MALMessageHeader header) {
        return supervisorAddresses.contains(NMFAccessControl.addressOf(header.getToURI()));
    }

    /**
     * A request to a service of the Supervisor: the service, the operation, and
     * the permission it needs.
     */
    static final class Call {

        final String service;
        final String operation;
        final String permission;
        private final boolean pubSub;

        private Call(String service, String operation, boolean pubSub) {
            this.service = service;
            this.operation = operation;
            this.permission = Permissions.SUPERVISOR + service;
            this.pubSub = pubSub;
        }

        /**
         * Returns the request that a message starts, or null if its area or
         * service is not known.
         */
        static Call of(final MALMessageHeader header) {
            try {
                MALArea malArea = MALContextFactory.lookupArea(header.getServiceArea(), header.getAreaVersion());
                ServiceInfo malService = malArea.getServiceByNumber(header.getService());
                MALOperation malOperation = malService.getOperationByNumber(header.getOperation());
                return new Call(malArea.getName().getValue() + "." + malService.getName().getValue(),
                        malOperation.getName().getValue(),
                        header.getInteractionType().getValue() == InteractionType.PUBSUB_VALUE);
            } catch (RuntimeException e) {
                return null;
            }
        }

        /**
         * Returns a request to a service that is not known, named by its
         * numbers, so that it can be logged and recorded.
         */
        static Call unknown(final MALMessageHeader header) {
            return new Call("area " + header.getServiceArea() + " version " + header.getAreaVersion()
                    + " service " + header.getService(), "operation " + header.getOperation(),
                    header.getInteractionType() != null
                    && header.getInteractionType().getValue() == InteractionType.PUBSUB_VALUE);
        }

        /** Whether it is a look-up of the Directory, which anyone may make. */
        boolean isLookup() {
            return "COM.Directory".equals(service) && "lookup".equals(operation);
        }

        /** Whether it is in the baseline that every App has: the Directory, the Heartbeat and the Apps Launcher events. */
        boolean isBaseline() {
            if ("COM.Directory".equals(service) || "SM.Heartbeat".equals(service)) {
                return true;
            }
            return "SM.AppsLauncher".equals(service) && "monitorEvents".equals(operation) && pubSub;
        }
    }

    /**
     * Returns the text of the error that an App receives for a permission it
     * was not granted, which tells its developer what to do.
     *
     * @param permission the missing permission
     * @return the text
     */
    public static String missingPermission(final String permission) {
        return "Missing permission: " + permission + ". Declare it in the <permissions> of the "
                + "nmf-package-maven-plugin in the App's pom.xml.";
    }

    private void refuse(AccessControlMode mode, MOErrorException error, String what, String app, Call call,
            MALMessageHeader header) throws MALCheckErrorException {
        if (record(mode, what, app, call, header, null)) {
            throw new MALCheckErrorException(error, null);
        }
    }

    /**
     * Logs and records a call that the mode refuses or audits.
     *
     * @return true if the call is refused, false if it is only audited
     */
    private boolean record(AccessControlMode mode, String what, String app, Call call,
            MALMessageHeader header, String entry) {
        boolean refused = (mode != AccessControlMode.AUDIT);
        Recorder r = recorder;
        if (r != null) {
            try {
                r.record(app, call.permission, call.operation, header.getFromURI(), entry, refused);
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "The unauthorised call could not be recorded", e);
            }
        }
        if (!refused) {
            LOGGER.log(Level.WARNING, "Audit: {0}; enforce mode would refuse it", what);
            return false;
        }
        LOGGER.log(Level.WARNING, "Refused: {0}", what);
        return true;
    }

    /**
     * Returns whether a message starts an interaction from a consumer: a send,
     * a submit, a request, an invoke, a progress, or a pub-sub register or
     * deregister. The other messages answer or continue one.
     *
     * @param header the header of the message
     * @return true if it starts an interaction
     */
    static boolean isInitiating(final MALMessageHeader header) {
        if (header.getInteractionType() == null) {
            return false;
        }
        int type = header.getInteractionType().getValue();
        if (type == InteractionType.SEND_VALUE) {
            return true;
        }
        int stage = (header.getInteractionStage() == null) ? -1 : header.getInteractionStage().getValue();
        if (type == InteractionType.PUBSUB_VALUE) {
            return stage == MALPubSubOperation._REGISTER_STAGE || stage == MALPubSubOperation._DEREGISTER_STAGE;
        }
        return stage == 1; // The first stage of a submit, request, invoke or progress
    }

    /**
     * Returns whether a message is from a publisher to a broker: a publish
     * register, a publish or a publish deregister.
     *
     * @param header the header of the message
     * @return true if a publisher sends it
     */
    static boolean isPublishing(final MALMessageHeader header) {
        if (header.getInteractionType() == null || header.getInteractionStage() == null
                || header.getInteractionType().getValue() != InteractionType.PUBSUB_VALUE
                || Boolean.TRUE.equals(header.getIsErrorMessage())) {
            return false;
        }
        short stage = header.getInteractionStage().getValue();
        return stage == MALPubSubOperation._PUBLISH_REGISTER_STAGE || stage == MALPubSubOperation._PUBLISH_STAGE
                || stage == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE;
    }

}
