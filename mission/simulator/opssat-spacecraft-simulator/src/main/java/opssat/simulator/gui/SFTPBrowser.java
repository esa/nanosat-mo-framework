/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
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
package opssat.simulator.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

public class SFTPBrowser extends JFrame implements Comparator<ChannelSftp.LsEntry> {

  private ChannelSftp channel;
  private String mode;
  private JTextField tfName;
  private GuiMainWindow mainGui;
  private StringBuilder sb;
  private JTable tableFiles;
  private boolean doubleClick = false;

  public SFTPBrowser(ChannelSftp chan, String mode, GuiMainWindow mainGui) {
    this.mainGui = mainGui;
    try {
      sb = new StringBuilder(chan.pwd());
    } catch (SftpException e) {
      e.printStackTrace();
    }
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setAlwaysOnTop(true);
    setTitle("Browse");
    this.channel = chan;
    this.mode = mode;
    SpringLayout springLayout = new SpringLayout();
    getContentPane().setLayout(springLayout);

    tableFiles = new JTable() {
      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        int rendererWidth = component.getPreferredSize().width;
        TableColumn tableColumn = getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width + 20,
            tableColumn.getPreferredWidth()));
        return component;
      }
    };
    tableFiles.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Name" }) {
      Class[] columnTypes = new Class[] { String.class };

      @Override
      public Class getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
      }

      boolean[] columnEditables = new boolean[] { false };

      @Override
      public boolean isCellEditable(int row, int column) {
        return columnEditables[column];
      }
    });
    tableFiles.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    JScrollPane scrollPane = new JScrollPane(tableFiles);
    springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.NORTH,
        getContentPane());
    springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST,
        getContentPane());
    springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -80, SpringLayout.SOUTH,
        getContentPane());
    springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST,
        getContentPane());
    getContentPane().add(scrollPane);

    // Fill table with initial data
    DefaultTableModel dtm = (DefaultTableModel) tableFiles.getModel();
    dtm.setRowCount(0); // clear table
    Vector<ChannelSftp.LsEntry> files;
    updateFileTable();

    // Add listener for double clicks on folders
    tableFiles.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        JTable table = (JTable) mouseEvent.getSource();
        Point point = mouseEvent.getPoint();
        int row = table.rowAtPoint(point);
        doubleClick = false;
        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {
          doubleClick = true;
        } else {
          Integer timerinterval = (Integer) Toolkit.getDefaultToolkit()
              .getDesktopProperty("awt.multiClickInterval");
          String folder = (String) table.getValueAt(row, 0);
          sb.append("/").append(folder);
          tfName.setText(sb.toString());
          Timer timer = new Timer(timerinterval, evt -> {
            if (doubleClick) {
              doubleClick = false; // reset flag
              updateFileTable();
            }
          });
          timer.setRepeats(false);
          timer.start();
        }
      }
    });

    JLabel lblName = new JLabel("Name:");
    springLayout.putConstraint(SpringLayout.NORTH, lblName, 5, SpringLayout.SOUTH, scrollPane);
    springLayout.putConstraint(SpringLayout.WEST, lblName, 0, SpringLayout.WEST, scrollPane);
    getContentPane().add(lblName);

    tfName = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, tfName, 0, SpringLayout.NORTH, lblName);
    springLayout.putConstraint(SpringLayout.WEST, tfName, 5, SpringLayout.EAST, lblName);
    springLayout.putConstraint(SpringLayout.EAST, tfName, 0, SpringLayout.EAST, scrollPane);
    getContentPane().add(tfName);
    tfName.setColumns(10);

    JButton btnSubmit = new JButton("Select");
    btnSubmit.addActionListener(arg0 -> {
      mainGui.setPathFromSFTP(sb.toString());
      SFTPBrowser.this.dispose();
    });
    springLayout.putConstraint(SpringLayout.NORTH, btnSubmit, 5, SpringLayout.SOUTH, tfName);
    springLayout.putConstraint(SpringLayout.EAST, btnSubmit, 0, SpringLayout.EAST, scrollPane);
    getContentPane().add(btnSubmit);
    this.getRootPane().setDefaultButton(btnSubmit);
  }

  public boolean isImg(ChannelSftp.LsEntry file) {
    String name = file.getFilename();
    String[] parts = name.split("\\.");
    if (parts.length < 2) {
      return false;
    }
    String extension = parts[parts.length - 1];
    ArrayList<String> extensions = new ArrayList<String>(
        Arrays.asList(new String[] { "jpg", "jpeg", "png", "bmp", "raw" }));
    return extensions.contains(extension);
  }

  private void updateFileTable() {
    DefaultTableModel dtm = (DefaultTableModel) tableFiles.getModel();
    dtm.setRowCount(0); // clear table
    Vector<ChannelSftp.LsEntry> files;
    try {
      files = channel.ls(sb.toString());
      Predicate<ChannelSftp.LsEntry> predicate;
      if (mode.equals("Fixed")) {
        predicate = x -> isImg(x) || x.getAttrs().isDir();
      } else {
        predicate = x -> x.getAttrs().isDir();
      }
      List<ChannelSftp.LsEntry> filteredFiles = files.stream().filter(predicate)
              .sorted((Comparator<ChannelSftp.LsEntry>) this).collect(Collectors.toList());
      for (ChannelSftp.LsEntry e : filteredFiles) {
        String key = e.getFilename();
        dtm.addRow(new String[] { key });
      }
    } catch (SftpException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public int compare(ChannelSftp.LsEntry arg0, ChannelSftp.LsEntry arg1) {
    return String.CASE_INSENSITIVE_ORDER.compare(arg0.getFilename(), arg1.getFilename());
  }

}
