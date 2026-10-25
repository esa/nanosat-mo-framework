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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
 * Only the Supervisor itself publishes on its services: it marks what its own
 * publishers send to its brokers, and the brokers refuse every other publisher.
 */
public final class AccessControlAuthority {

    private static final Logger LOGGER = Logger.getLogger(AccessControlAuthority.class.getName());

    /** A known caller: an App with what it was granted, or ground. */
    private static final class Caller {

        private final String app;
        private final Set<String> granted;

        Caller(String app, Collection<String> granted) {
            this.app = app;
            this.granted = Collections.unmodifiableSet(new HashSet<>(granted));
        }

        boolean isGround() {
            return app == null;
        }
    }

    /** Records each call made without the permission or credential it needs. */
    public interface Recorder {

        /**
         * Records a call made without the permission or credential it needs.
         *
         * @param app the App that made it, or null if the caller had no valid credential
         * @param permission the permission it needs
         * @param operation the operation called
         * @param from the URI of the caller
         * @param refused true if it was refused, false if it was only audited
         */
        void record(String app, String permission, String operation, URI from, boolean refused);
    }

    private final Set<String> supervisorAddresses = new HashSet<>();
    private volatile Recorder recorder;
    private final Map<String, Caller> callers = new ConcurrentHashMap<>();
    private final Map<String, String> credentialOfApp = new ConcurrentHashMap<>();
    /** The mark of the Supervisor's own publishers, which never leaves the Supervisor. */
    private final byte[] ownPublisherMark = NMFAccessControl.newCredential();
    private final String ownPublisherKey = NMFAccessControl.toText(ownPublisherMark);

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
        callers.put(NMFAccessControl.toText(secret), new Caller(null, Collections.emptySet()));
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
        callers.put(key, new Caller(app, granted));
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

        Caller caller = callers.get(credentialOf(header.getAuthenticationId()));
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
     * Marks a message that one of the Supervisor's own publishers sends to one
     * of its brokers, so that the broker accepts it. The NMF access control
     * calls this only for the messages that the Supervisor sends; the mark is
     * not given out, so no other process can make it.
     *
     * @param header the header of the message that the Supervisor sends
     */
    public void markOwnPublishing(final MALMessageHeader header) {
        if (isPublishing(header) && isToSupervisor(header)) {
            header.setAuthenticationId(new Blob(ownPublisherMark.clone()));
        }
    }

    /**
     * Refuses a publisher's message to a broker of the Supervisor, unless one of
     * the Supervisor's own publishers sent it.
     */
    private void checkPublishing(AccessControlMode mode, MALMessageHeader header) throws MALCheckErrorException {
        if (ownPublisherKey.equals(credentialOf(header.getAuthenticationId()))) {
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
        boolean refused = (mode != AccessControlMode.AUDIT);
        Recorder r = recorder;
        if (r != null) {
            try {
                r.record(app, call.permission, call.operation, header.getFromURI(), refused);
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING, "The unauthorised call could not be recorded", e);
            }
        }
        if (!refused) {
            LOGGER.log(Level.WARNING, "Audit: {0}; enforce mode would refuse it", what);
            return;
        }
        LOGGER.log(Level.WARNING, "Refused: {0}", what);
        throw new MALCheckErrorException(error, null);
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

    private static String credentialOf(Blob blob) {
        if (NMFAccessControl.isEmpty(blob)) {
            return "";
        }
        return NMFAccessControl.toText(blob.getValue());
    }
}
