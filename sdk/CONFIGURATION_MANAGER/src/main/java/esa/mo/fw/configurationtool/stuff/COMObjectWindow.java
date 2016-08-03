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
package esa.mo.fw.configurationtool.stuff;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.tools.mowindow.MOWindow;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMObject;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Element;

/**
 *
 * @author Cesar Coelho
 */
public final class COMObjectWindow extends javax.swing.JDialog {

    private final ArchivePersistenceObject comObject;
    private final boolean editable;
    private final ArchiveStub archiveService;

    /**
     * Creates new form MOWindow
     *
     * @param comObject
     * @param editable
     * @param archiveService
     */
    public COMObjectWindow(ArchivePersistenceObject comObject, boolean editable, ArchiveStub archiveService) {
        initComponents();

        if (comObject == null) {
            Logger.getLogger(COMObjectWindow.class.getName()).log(Level.SEVERE, "A null object was submitted into the COMObjectWindow. The COM object will not be displayed.");
        }

        this.setModal(true);
        this.setLocationRelativeTo(null);

        this.comObject = comObject;
        this.editable = editable;
        this.archiveService = archiveService;

        // Set the object Body type field
        if (comObject.getObject() != null) {
            this.tfObjectBodyType.setText(comObject.getObject().getClass().getSimpleName());
        } else {
            this.tfObjectBodyType.setText("null");
            this.objectBodyButton.setEnabled(false);
        }

        this.tfDomain.setEditable(editable);
        this.tfObjId.setEditable(editable);

        this.tfObjectType.setEditable(false);
        this.tfObjectType1.setEditable(editable);
        this.tfObjectType2.setEditable(editable);
        this.tfObjectType3.setEditable(editable);
        this.tfObjectType4.setEditable(editable);

        this.tfDomain.setText(comObject.getDomainId());
        this.tfObjId.setText(comObject.getObjectId().toString());

        this.tfObjectType.setText(HelperCOM.objType2string(comObject.getObjectType()));
        this.tfObjectType1.setText(comObject.getObjectType().getArea().toString());
        this.tfObjectType2.setText(comObject.getObjectType().getService().toString());
        this.tfObjectType3.setText(comObject.getObjectType().getVersion().toString());
        this.tfObjectType4.setText(comObject.getObjectType().getNumber().toString());

        ArchiveDetails archiveDetails = comObject.getArchiveDetails();

        if (archiveDetails != null) {

            if (archiveDetails.getNetwork() != null) {
                this.tfNetwork.setText(archiveDetails.getNetwork().toString());
            } else {
                this.tfNetwork.setText("null");
            }

            if (archiveDetails.getTimestamp() != null) {
                this.tfTimestamp.setText(HelperTime.time2readableString(archiveDetails.getTimestamp()));
            } else {
                this.tfProvider.setText("null");
            }

            if (archiveDetails.getProvider() != null) {
                this.tfProvider.setText(archiveDetails.getProvider().toString());
            } else {
                this.tfProvider.setText("null");
            }

            Long related = archiveDetails.getDetails().getRelated();

            if (related == null) {
                this.relatedObjId.setText("null");
                this.relatedButton.setEnabled(false);
            } else {
                COMObject comObjectInfo = HelperCOM.objType2COMObject(comObject.getObjectType());

                if (comObjectInfo.hasRelated()) {
                    if (comObjectInfo.getRelatedType() != null){
                        this.relatedType.setText(HelperCOM.objType2string(comObjectInfo.getRelatedType()));
                        this.relatedType1.setText(comObjectInfo.getRelatedType().getArea().toString());
                        this.relatedType2.setText(comObjectInfo.getRelatedType().getService().toString());
                        this.relatedType3.setText(comObjectInfo.getRelatedType().getVersion().toString());
                        this.relatedType4.setText(comObjectInfo.getRelatedType().getNumber().toString());
                    }else{
                        this.relatedType.setText("no info");  // Problem needs to be fixed!! Why no information available?
                        this.relatedButton.setEnabled(false);
                    }
                }

                this.relatedObjId.setText(related.toString());
            }

            ObjectId source = archiveDetails.getDetails().getSource();

            if (source == null) {
                this.sourceType.setText("null");
                this.sourceButton.setEnabled(false);
            } else {
                // Source
                this.sourceType.setText(HelperCOM.objType2string(comObject.getArchiveDetails().getDetails().getSource().getType()));
                this.sourceType1.setText(comObject.getArchiveDetails().getDetails().getSource().getType().getArea().toString());
                this.sourceType2.setText(comObject.getArchiveDetails().getDetails().getSource().getType().getService().toString());
                this.sourceType3.setText(comObject.getArchiveDetails().getDetails().getSource().getType().getVersion().toString());
                this.sourceType4.setText(comObject.getArchiveDetails().getDetails().getSource().getType().getNumber().toString());

                this.sourceDomain.setText(HelperMisc.domain2domainId(comObject.getArchiveDetails().getDetails().getSource().getKey().getDomain()));
                this.sourceObjId.setText(comObject.getArchiveDetails().getDetails().getSource().getKey().getInstId().toString());
            }
        }

        if (editable) {
            this.button.setText("Submit");
        } else {
            this.button.setText("Close");
        }

        this.setVisible(true);
//        this.validate();
//        this.repaint();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        objIdentification = new javax.swing.JLabel();
        objIdentification1 = new javax.swing.JLabel();
        objIdentification2 = new javax.swing.JLabel();
        tfDomain = new javax.swing.JTextField();
        tfObjectType = new javax.swing.JTextField();
        tfObjId = new javax.swing.JTextField();
        tfObjectType1 = new javax.swing.JTextField();
        tfObjectType2 = new javax.swing.JTextField();
        tfObjectType3 = new javax.swing.JTextField();
        tfObjectType4 = new javax.swing.JTextField();
        componentsPanel = new javax.swing.JPanel();
        objectBodyButton = new javax.swing.JButton();
        relatedButton = new javax.swing.JButton();
        sourceButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfObjectBodyType = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        relatedObjId = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sourceType = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tfTimestamp = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tfNetwork = new javax.swing.JTextField();
        tfProvider = new javax.swing.JTextField();
        sourceDomain = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        sourceObjId = new javax.swing.JTextField();
        sourceType3 = new javax.swing.JTextField();
        sourceType4 = new javax.swing.JTextField();
        sourceType1 = new javax.swing.JTextField();
        sourceType2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        relatedType1 = new javax.swing.JTextField();
        relatedType2 = new javax.swing.JTextField();
        relatedType3 = new javax.swing.JTextField();
        relatedType4 = new javax.swing.JTextField();
        relatedType = new javax.swing.JTextField();
        bottomPanel = new javax.swing.JPanel();
        button = new javax.swing.JToggleButton();

        setTitle("COM Object");
        setMinimumSize(new java.awt.Dimension(650, 550));
        setResizable(false);

        topPanel.setPreferredSize(new java.awt.Dimension(78, 100));

        objIdentification.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objIdentification.setText("Domain:");
        objIdentification.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        objIdentification1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objIdentification1.setText("Object Type:");
        objIdentification1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        objIdentification2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objIdentification2.setText("Object Instance Identifier:");
        objIdentification2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        tfObjectType1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        tfObjectType2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfObjectType2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfObjectType2ActionPerformed(evt);
            }
        });

        tfObjectType3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        tfObjectType4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addComponent(objIdentification, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDomain))
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addComponent(objIdentification1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjectType1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjectType2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjectType3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjectType4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tfObjectType, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addComponent(objIdentification2, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjId)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(objIdentification)
                    .addComponent(tfDomain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(objIdentification1)
                    .addComponent(tfObjectType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfObjectType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfObjectType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfObjectType3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfObjectType4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(objIdentification2)
                    .addComponent(tfObjId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        componentsPanel.setMinimumSize(new java.awt.Dimension(550, 100));
        componentsPanel.setName(""); // NOI18N
        componentsPanel.setPreferredSize(new java.awt.Dimension(550, 200));

        objectBodyButton.setText("View Object Body");
        objectBodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                objectBodyButtonActionPerformed(evt);
            }
        });

        relatedButton.setText("Retrieve Related");
        relatedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relatedButtonActionPerformed(evt);
            }
        });

        sourceButton.setText("Retrieve Source");
        sourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Object Body Type:");

        jLabel2.setText("Object Instance Id:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Related");

        sourceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceTypeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Source");

        jLabel5.setText("Object Type:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Object Body");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Other Details");

        jLabel8.setText("Network:");

        jLabel9.setText("Timestamp:");

        jLabel10.setText("Provider:");

        jLabel11.setText("Domain:");

        jLabel12.setText("Object Instance Identifier:");

        sourceType3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        sourceType4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        sourceType1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        sourceType2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel13.setText("Object Type:");

        relatedType1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        relatedType2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        relatedType3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        relatedType4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        relatedType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relatedTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout componentsPanelLayout = new javax.swing.GroupLayout(componentsPanel);
        componentsPanel.setLayout(componentsPanelLayout);
        componentsPanelLayout.setHorizontalGroup(
            componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(componentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfProvider, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(tfNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(tfTimestamp, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(componentsPanelLayout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(componentsPanelLayout.createSequentialGroup()
                                .addGap(63, 63, 63)
                                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sourceDomain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(sourceButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(componentsPanelLayout.createSequentialGroup()
                                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(componentsPanelLayout.createSequentialGroup()
                                                .addComponent(sourceType1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sourceType2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sourceType3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sourceType4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sourceType, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(sourceObjId, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfObjectBodyType, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(objectBodyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                        .addGap(62, 62, 62)
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(componentsPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(relatedObjId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(componentsPanelLayout.createSequentialGroup()
                                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(relatedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(componentsPanelLayout.createSequentialGroup()
                                        .addComponent(relatedType1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(relatedType2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(relatedType3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(relatedType4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(relatedType, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        componentsPanelLayout.setVerticalGroup(
            componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, componentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfObjectBodyType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(objectBodyButton))
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(relatedType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(relatedType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(relatedType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(relatedType3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(relatedType4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(relatedObjId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(relatedButton)
                .addGap(18, 18, 18)
                .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(componentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sourceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceType2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceType3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sourceType4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceDomain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceObjId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(componentsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfTimestamp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceButton)
                .addGap(110, 110, 110))
        );

        getContentPane().add(componentsPanel, java.awt.BorderLayout.CENTER);

        bottomPanel.setMaximumSize(new java.awt.Dimension(30, 32767));
        bottomPanel.setName(""); // NOI18N
        bottomPanel.setPreferredSize(new java.awt.Dimension(452, 40));

        button.setText("Submit");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(button)
                .addContainerGap())
        );

        getContentPane().add(bottomPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionPerformed

        this.setVisible(false);

    }//GEN-LAST:event_buttonActionPerformed

    private void objectBodyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_objectBodyButtonActionPerformed

        // Open the body object
        if (this.comObject != null) {
            Element object = (Element) HelperAttributes.javaType2Attribute(this.comObject.getObject());
            if (object != null) {
                MOWindow objectBodyWindow = new MOWindow(object, false);
            }
        }

    }//GEN-LAST:event_objectBodyButtonActionPerformed

    private void relatedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relatedButtonActionPerformed

        COMObject comObjectInfo = HelperCOM.objType2COMObject(comObject.getObjectType());

        ArchivePersistenceObject relatedCOMObject = HelperArchive.getArchiveCOMObject(
                archiveService,
                comObjectInfo.getRelatedType(),
                comObject.getDomain(),
                comObject.getArchiveDetails().getDetails().getRelated()
        );

        COMObjectWindow newWindow = new COMObjectWindow(relatedCOMObject, editable, archiveService);

    }//GEN-LAST:event_relatedButtonActionPerformed

    private void sourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceButtonActionPerformed

        ArchivePersistenceObject sourceCOMObject = HelperArchive.getArchiveCOMObject(
                archiveService,
                comObject.getArchiveDetails().getDetails().getSource().getType(),
                comObject.getArchiveDetails().getDetails().getSource().getKey().getDomain(),
                comObject.getArchiveDetails().getDetails().getSource().getKey().getInstId()
        );

        COMObjectWindow newWindow = new COMObjectWindow(sourceCOMObject, editable, archiveService);

    }//GEN-LAST:event_sourceButtonActionPerformed

    private void tfObjectType2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfObjectType2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfObjectType2ActionPerformed

    private void sourceTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sourceTypeActionPerformed

    private void relatedTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relatedTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_relatedTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JToggleButton button;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel objIdentification;
    private javax.swing.JLabel objIdentification1;
    private javax.swing.JLabel objIdentification2;
    private javax.swing.JButton objectBodyButton;
    private javax.swing.JButton relatedButton;
    private javax.swing.JTextField relatedObjId;
    private javax.swing.JTextField relatedType;
    private javax.swing.JTextField relatedType1;
    private javax.swing.JTextField relatedType2;
    private javax.swing.JTextField relatedType3;
    private javax.swing.JTextField relatedType4;
    private javax.swing.JButton sourceButton;
    private javax.swing.JTextField sourceDomain;
    private javax.swing.JTextField sourceObjId;
    private javax.swing.JTextField sourceType;
    private javax.swing.JTextField sourceType1;
    private javax.swing.JTextField sourceType2;
    private javax.swing.JTextField sourceType3;
    private javax.swing.JTextField sourceType4;
    private javax.swing.JTextField tfDomain;
    private javax.swing.JTextField tfNetwork;
    private javax.swing.JTextField tfObjId;
    private javax.swing.JTextField tfObjectBodyType;
    private javax.swing.JTextField tfObjectType;
    private javax.swing.JTextField tfObjectType1;
    private javax.swing.JTextField tfObjectType2;
    private javax.swing.JTextField tfObjectType3;
    private javax.swing.JTextField tfObjectType4;
    private javax.swing.JTextField tfProvider;
    private javax.swing.JTextField tfTimestamp;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    public ArchivePersistenceObject getCOMObject() {
        return comObject;
    }

    public Element getObjectBody() {
        return (Element) HelperAttributes.javaType2Attribute(comObject.getObject());
    }

}
