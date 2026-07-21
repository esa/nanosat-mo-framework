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
package esa.mo.nmf.ctt.services.mc;

import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import java.awt.Color;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.consumer.MonitorValueSubscriptionKeys;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ValidityState;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterPublishedValues extends javax.swing.JPanel {

    final ParameterConsumerServiceImpl parameterService;
    private final int numberOfColumns = 5;
    private final ParameterLabel[] labels = new ParameterLabel[32 * numberOfColumns];
    private Subscription subscription;

    // Display slot assigned to each parameter, on a first-come-first-served
    // basis: the panel fills up in the order the first update of each parameter
    // arrives, so it shows something even when there are more parameters than it
    // can hold (and their object instance ids do not fit the fixed grid).
    private final java.util.Map<Long, Integer> slotByParamId = new java.util.HashMap<>();

    public ParameterLabel[] getLabels() {
        return this.labels;
    }

    public ParameterPublishedValues(final ParameterConsumerServiceImpl parameterService) {
        this.parameterService = parameterService;
        this.setEnabled(false);
        this.setPreferredSize(new java.awt.Dimension(800, 600));
        this.setLayout(new java.awt.GridLayout(32, 16, 1, 1));

        final java.awt.Dimension dim = new java.awt.Dimension(64, 16);
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = new ParameterLabel(i);
            labels[i].setMinimumSize(dim);
            labels[i].setPreferredSize(dim);
            labels[i].setMaximumSize(dim);
            labels[i].setOpaque(true);
            labels[i].setBackground(Color.WHITE);
            labels[i].setForeground(Color.GREEN);
            labels[i].setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }

        // Info
        labels[0 * numberOfColumns].setNewValue("Obj Instance Id", false);
        labels[1 * numberOfColumns].setNewValue("Validity State", false);
        labels[2 * numberOfColumns].setNewValue("Raw Value", false);
        labels[3 * numberOfColumns].setNewValue("Converted Value", false);

        for (int i = 0; i < labels.length; ++i) {
            this.add(labels[i]);
        }
    }

    public void subscribeToParameters() throws MALInteractionException, MALException {
        // Subscribe to ParametersValues
        subscription = ConnectionConsumer.subscriptionWildcardRandom();
        this.parameterService.getParameterStub().monitorValueRegister(subscription, new ParameterConsumerAdapter());
    }

    public void removeNotify() {
        super.removeNotify();
        IdentifierList ids = new IdentifierList();
        ids.add(subscription.getSubscriptionId());
        try {
            parameterService.getParameterStub().monitorValueDeregister(ids);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ParameterPublishedValues.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the display slot assigned to a parameter, assigning the next free
     * one the first time the parameter is seen (first come, first served), or
     * {@code null} once the panel is full. Synchronized because updates may be
     * delivered from several MAL threads.
     *
     * @param parameterId The parameter object instance id.
     * @return The assigned slot, or {@code null} if the panel is full.
     */
    private synchronized Integer slotForParameter(final Long parameterId) {
        Integer slot = slotByParamId.get(parameterId);
        if (slot != null) {
            return slot;
        }
        // Slot 0 (grid index 0) holds the header legend, so parameters start at
        // slot 1.
        final int candidate = slotByParamId.size() + 1;
        // Reject the slot if any of its four label cells would fall outside the
        // fixed grid.
        if (slotToIndex(candidate) + 3 * numberOfColumns >= labels.length) {
            return null;
        }
        slotByParamId.put(parameterId, candidate);
        return candidate;
    }

    /**
     * Maps a display slot to the index of its first label cell in the grid.
     *
     * @param slot The display slot.
     * @return The index of the slot's first label.
     */
    private int slotToIndex(final int slot) {
        return (5 * numberOfColumns) * (slot / numberOfColumns) + slot % numberOfColumns;
    }

    public class ParameterConsumerAdapter extends ParameterAdapter {

        @Override
        public void monitorValueNotifyReceived(final MALMessageHeader msgHeader,
                final Identifier lIdentifier, final UpdateHeader updateHeader,
                final MonitorValueSubscriptionKeys keys,
                final ParameterValue parameterValue,
                final Map qosp) {
            Logger.getLogger(ParameterPublishedValues.class.getName()).log(
                    Level.FINE, "Received update parameter value!");

            final String name = Attribute.attribute2string(keys.getName());
            final Long parameterId = keys.getParameterId();

            try {
                final int objId = parameterId.intValue();

                // First come, first served: place the parameter in the order its
                // first update arrives, not by its object instance id, so the
                // panel is not left blank when the ids overflow the grid.
                final Integer slot = slotForParameter(parameterId);
                if (slot == null) {
                    return; // The panel is already full.
                }
                final int index = slotToIndex(slot);

                String nameId = "(" + String.valueOf(objId) + ") " + name;
                ValidityState validityState = parameterValue.getValidityState();
                String validity = validityState.toString();
                String rawValueStr = Attribute.attribute2string(parameterValue.getRawValue());
                final String rawValue = rawValueStr.isEmpty() ? "\"\"" : rawValueStr;
                String convertedValue = Attribute.attribute2string(parameterValue.getConvertedValue());

                boolean isNotValid = ((int) validityState.getValue() != ValidityState.VALID_VALUE);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    labels[index + 0 * numberOfColumns].setNewValue(nameId, isNotValid);
                    labels[index + 1 * numberOfColumns].setNewValue(validity, isNotValid);
                    labels[index + 2 * numberOfColumns].setNewValue(rawValue, isNotValid);
                    labels[index + 3 * numberOfColumns].setNewValue(convertedValue, isNotValid);
                });
            } catch (NumberFormatException ex) {
                Logger.getLogger(ParameterPublishedValues.class.getName()).log(Level.WARNING,
                        "Error decoding update with name: {0}", name);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 795,
            Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 496,
            Short.MAX_VALUE));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
