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
package esa.mo.tools.mowindow;

import esa.mo.helpertools.helpers.HelperMisc;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Composite;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Enumeration;
import org.ccsds.moims.mo.mal.structures.Union;

/**
 *
 * @author Cesar Coelho
 */
public final class MOWindow extends javax.swing.JDialog {

    private final Object receivedObj;
    private final boolean editable;
    private boolean closeButtonPressed = false;

    /**
     * Creates new form MOWindow
     *
     * @param obj
     * @param editable
     */
    public MOWindow(Object obj, boolean editable) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        componentsPanel.setLayout(new BoxLayout(componentsPanel, BoxLayout.PAGE_AXIS));

        String className = obj.getClass().getSimpleName();
        this.setTitle(className);  // Set Title
        this.receivedObj = obj;
        this.editable = editable;

        if (obj instanceof Element) {
            MALArea area = MALContextFactory.lookupArea(((Element) obj).getAreaNumber(), ((Element) obj).getAreaVersion());
            MALService service = area.getServiceByNumber(((Element) obj).getServiceNumber());
            String string;

            if (service != null) {
                string = "Area: " + area.getName() + "        Service: " + service.getName() + "        TypeShortForm: " + ((Element) obj).getTypeShortForm();
            } else {
                string = "Area: " + area.getName() + "          TypeShortForm: " + ((Element) obj).getTypeShortForm();
            }

            objIdentification.setText(string);
        }

        if (editable) {
            this.button.setText("Submit");
        } else {
            this.button.setText("Close");
        }

        this.interpretReceivedObj(obj, editable);

        this.refreshVerticalSize();
        this.refreshHorizontalSize();

        bottomPanel.validate();
        bottomPanel.repaint();

        this.setVisible(true);
        this.validate();
        this.repaint();

    }

    private void interpretReceivedObj(Object obj, boolean editable) {

        // Is the object a List?
        if (obj instanceof ElementList) {

            ElementList list = (ElementList) obj;

            for (int i = 0; i < list.size(); i++) {

                if (!(list.get(i) instanceof Element)) {
                    if (list.get(i) == null) {
                        try {
                            Element something = HelperMisc.elementList2element(list);
                            MOelementList moElementList = new MOelementList(this, String.valueOf(componentsPanel.getComponentCount()), something, editable, true);
                            componentsPanel.add(moElementList);
                        } catch (Exception ex) {
                            Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        MOelementList moElementList = new MOelementList(this, String.valueOf(componentsPanel.getComponentCount()), FieldsHandler.filterRawObject(list.get(i)), editable, (list.get(i) == null));
                        componentsPanel.add(moElementList);
                    }
                } else {
                    MOelementList moElementList = new MOelementList(this, String.valueOf(componentsPanel.getComponentCount()), (Element) list.get(i), editable, (list.get(i) == null));
                    componentsPanel.add(moElementList);
                }

            }

            java.awt.event.ActionListener actionListener = new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonAddActionPerformed(evt);
                }
            };

            MOelementListBlank moElementListBlank = new MOelementListBlank(actionListener, editable);
            componentsPanel.add(moElementListBlank);
            return;

        }

        // Is the object a composite?
        if (obj instanceof Composite) { // Is Composite...

            Field[] fields = FieldsHandler.getDeclaredFields(obj);

            if (fields.length < 6) {
                return;
            }

            for (int i = 6; i < fields.length; i++) {
                boolean fieldObjectIsNull = FieldsHandler.isFieldNull(fields[i], obj);
                Object fieldObject = FieldsHandler.generateFieldObject(fields[i], obj);

                // If another Composite add a button to create another MOWindow
                if (fieldObject instanceof Composite) {
                    MOcomposite moComposite = new MOcomposite(fields[i].getName(), (Element) fieldObject, editable, fieldObjectIsNull);
                    componentsPanel.add(moComposite);
                    continue;
                }

                if (fieldObject instanceof Attribute) {
                    MOattribute moField = new MOattribute(fields[i].getName(), (Element) fieldObject, editable, fieldObjectIsNull);
                    componentsPanel.add(moField);
                    continue;
                }

                if (fieldObject instanceof Enumeration) {
                    MOenumeration moField = new MOenumeration(fields[i].getName(), (Element) fieldObject, editable, fieldObjectIsNull);
                    componentsPanel.add(moField);
                    continue;
                }

                if (fieldObject == null) {  // It is unknown or type "Attribute"
                    MOattribute moField = new MOattribute(fields[i].getName(), (Element) fieldObject, editable, fieldObjectIsNull);
                    componentsPanel.add(moField);
                    continue;
                }

                if (!(fieldObject instanceof Element)) {
                    MOattribute moField = new MOattribute(fields[i].getName(), FieldsHandler.filterRawObject(fieldObject), editable, fieldObjectIsNull);
                    componentsPanel.add(moField);
                    continue;
                }
            }

            return;
        }

        MOattribute field = new MOattribute("value", obj, editable, false);
        componentsPanel.add(field);

    }

    public void refreshVerticalSize() {
        this.setSize(this.getWidth(), componentsPanel.getComponentCount() * 23 + 110);

        this.validate();
        this.repaint();
    }

    public void refreshHorizontalSize() {

        for (int i = 0; i < componentsPanel.getComponentCount(); i++) {
            String paramType = ((MOelement) componentsPanel.getComponent(i)).getFieldTypeString();

            // Calculate the size we want...
            int horizontalSize = paramType.length() * 8 + 450;

            // Is the value greater than the current one?
            if (horizontalSize > this.getWidth()) {
                this.setSize(horizontalSize, this.getHeight());
                this.validate();
                this.repaint();
            }
        }

    }

    public javax.swing.JPanel getComponentsPanel() {
        return this.componentsPanel;
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
        componentsPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        button = new javax.swing.JToggleButton();

        setMinimumSize(new java.awt.Dimension(600, 120));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

        topPanel.setPreferredSize(new java.awt.Dimension(78, 40));

        objIdentification.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        objIdentification.setText("objIdentification");
        objIdentification.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(objIdentification, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(objIdentification)
                .addContainerGap())
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        componentsPanel.setMinimumSize(new java.awt.Dimension(550, 100));
        componentsPanel.setName(""); // NOI18N
        componentsPanel.setPreferredSize(new java.awt.Dimension(550, 200));
        componentsPanel.setLayout(new java.awt.GridLayout(1, 0, 0, 15));
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
            .addComponent(button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // It goes in this one when you press the "x" on the corner
        closeButtonPressed = true;
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        closeButtonPressed = true;
    }//GEN-LAST:event_formWindowClosed

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
//        closeButtonPressed = true;
    }//GEN-LAST:event_formWindowDeactivated

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JToggleButton button;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JLabel objIdentification;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Element element = HelperMisc.elementList2element((ElementList) this.receivedObj);

            MOelementList moElementList = new MOelementList(this, String.valueOf(componentsPanel.getComponentCount() - 1), element, this.editable, (this.receivedObj == null));
            componentsPanel.add(moElementList, componentsPanel.getComponentCount() - 1);
            this.refreshVerticalSize();
        } catch (Exception ex) {
            Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    public Object getObject() throws InterruptedIOException {

        if (closeButtonPressed) {
            throw new java.io.InterruptedIOException();
        }

        if (!editable) {
            return this.receivedObj;
        }

        if (this.receivedObj instanceof ElementList) {
            ElementList list = (ElementList) ((ElementList) this.receivedObj).createElement();

            for (int i = 0; i < componentsPanel.getComponentCount() - 1; i++) {
                list.add(((MOelementList) componentsPanel.getComponent(i)).getObject());
            }

            return list;
        }

        // Composite not list
        if (this.receivedObj instanceof Composite && !(this.receivedObj instanceof ElementList)) {

            Field[] fields = FieldsHandler.getDeclaredFields(this.receivedObj);

            if (fields.length < 6) {
                return null;
            }

            for (int i = 0; i < componentsPanel.getComponentCount(); i++) {

                fields[i + 6].setAccessible(true);
                MOelement element = ((MOelement) componentsPanel.getComponent(i));
                Object object = element.getObject();

                if (object instanceof Union) {
                    try {
                        Field fieldUnion = object.getClass().getDeclaredField("value");
                        fieldUnion.setAccessible(true);

                        if (fields[i + 6].getType().getSimpleName().equals("Attribute")) {
                            fields[i + 6].set(this.receivedObj, object);
                        } else {
                            fields[i + 6].set(this.receivedObj, fieldUnion.get(object));
                        }

                    } catch (NoSuchFieldException ex1) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (SecurityException ex1) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (IllegalArgumentException ex1) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex1);
                    } catch (IllegalAccessException ex1) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                } else {
                    try {
                        fields[i + 6].set(this.receivedObj, object);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(MOWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            return this.receivedObj;
        }

        if (this.receivedObj instanceof Attribute && !(this.receivedObj instanceof ElementList)) {
            return ((MOattribute) componentsPanel.getComponent(0)).getObject();
        }

        return ((MOattribute) componentsPanel.getComponent(0)).getObject();

//        return null;
    }

}
