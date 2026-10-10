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
package esa.mo.nmf.security;

import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;
import org.junit.Assert;
import org.junit.Test;

/**
 * NMF-2026-002: unsafe deserialization of parameter and action values (CWE-502).
 * <p>
 * An NMF Monitor and Control adapter deserializes an attacker-controlled
 * {@code Blob} value with {@code ObjectInputStream.readObject()} and no
 * allow-list, so the sender chooses which classes are instantiated and which
 * deserialization callbacks run in the victim process. This was demonstrated by
 * Thales at Hack CYSAT 2023. See ADVISORIES.md.
 * <p>
 * The test drives the provider-side sink, {@code SimpleMonitorAndControlAdapter
 * .onSetValue}, with a {@link Canary} that is not a type the application
 * exchanges, and asserts that it is <strong>not</strong> deserialized. The
 * canary is benign: its {@code readObject} only records that it ran, so the test
 * detects the flaw without running any attacker code.
 * <p>
 * <strong>This test fails while the vulnerability is open</strong>, which is the
 * point: the current framework does deserialize the canary. It passes once the
 * deserialization of untrusted values is removed or restricted with an
 * {@code ObjectInputFilter}.
 */
public class Nmf2026002Test {

    private static volatile boolean canaryDeserialized;

    @Test
    public void setValueMustNotDeserializeAnUntrustedBlob() throws Exception {
        canaryDeserialized = false;

        // Craft a Blob raw value exactly as a remote peer would: a serialized object.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new Canary());
        }
        Blob attackerValue = new Blob(bytes.toByteArray());

        IdentifierList identifiers = new IdentifierList();
        identifiers.add(new Identifier("anyParameter"));
        ParameterRawValueList values = new ParameterRawValueList();
        values.add(new ParameterRawValue(0L, attackerValue));

        // Drive the provider-side sink: this is what a remote setValue reaches.
        new NoOpAdapter().onSetValue(identifiers, values);

        Assert.assertFalse(
                "NMF-2026-002 (CWE-502): SimpleMonitorAndControlAdapter.onSetValue deserialized an "
                + "attacker-controlled Blob and ran the canary's readObject. A Blob raw value must not be "
                + "passed to ObjectInputStream.readObject without an allow-list / ObjectInputFilter. This "
                + "test fails until the flaw is fixed.",
                canaryDeserialized);
    }

    /** A harmless object that records only whether it was deserialized. */
    public static final class Canary implements Serializable {

        private static final long serialVersionUID = 1L;

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            canaryDeserialized = true;
        }
    }

    /** A minimal concrete adapter; the overridden simple methods are not exercised. */
    private static final class NoOpAdapter extends SimpleMonitorAndControlAdapter {

        @Override
        public boolean onSetValueSimple(String name, Serializable value) {
            return true;
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            return null;
        }

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
            return true;
        }
    }
}
