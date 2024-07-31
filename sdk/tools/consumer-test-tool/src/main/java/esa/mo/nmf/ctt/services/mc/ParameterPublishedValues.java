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
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import java.awt.Color;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ValidityState;

/**
 *
 * @author Cesar Coelho
 */
public class ParameterPublishedValues extends javax.swing.JPanel {

    final ParameterConsumerServiceImpl parameterService;
    private final int numberOfColumns = 5;
    private final ParameterLabel[] labels = new ParameterLabel[32 * numberOfColumns];
    private Subscription subscription;

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
        subscription = ConnectionConsumer.subscriptionWildcard();
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

    public class ParameterConsumerAdapter extends ParameterAdapter {

        @Override
        public void monitorValueNotifyReceived(final MALMessageHeader msgHeader, final Identifier lIdentifier,
            final UpdateHeaderList lUpdateHeaderList, final ObjectIdList lObjectIdList,
            final ParameterValueList lParameterValueList, final Map qosp) {
            Logger.getLogger(ParameterPublishedValues.class.getName()).log(Level.FINE,
                "Received update parameters list of size : {0}", lObjectIdList.size());

            for (int i = 0; i < lObjectIdList.size(); i++) {
                final UpdateHeader updateHeader = lUpdateHeaderList.get(i);
                final ParameterValue parameterValue = lParameterValueList.get(i);
                final String name = updateHeader.getKey().getFirstSubKey().getValue();

                try {
                    final int objId = updateHeader.getKey().getSecondSubKey().intValue();

                    final int index = (int) ((5 * numberOfColumns) * Math.floor(objId / (5.0)) + objId %
                        numberOfColumns);

                    if ((0 <= index) && (index < labels.length)) {
                        String nameId = "(" + objId + ") " + updateHeader.getKey().getFirstSubKey().getValue();
                        UOctet validityState = parameterValue.getValidityState();
                        String validity = ValidityState.fromNumericValue(new UInteger(validityState.getValue()))
                            .toString();
                        String rawValue = HelperAttributes.attribute2string(parameterValue.getRawValue());
                        String convertedValue = HelperAttributes.attribute2string(parameterValue.getConvertedValue());

                        boolean isNotValid = (validityState.getValue() != ValidityState._VALID_INDEX);
                        labels[index + 0 * numberOfColumns].setNewValue(nameId, isNotValid);
                        labels[index + 1 * numberOfColumns].setNewValue(validity, isNotValid);
                        labels[index + 2 * numberOfColumns].setNewValue(rawValue, isNotValid);
                        labels[index + 3 * numberOfColumns].setNewValue(convertedValue, isNotValid);
                    }
                } catch (NumberFormatException ex) {
                    Logger.getLogger(ParameterPublishedValues.class.getName()).log(Level.WARNING,
                        "Error decoding update with name: {0}", name);
                }
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
