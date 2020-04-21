/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2016      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under the European Space Agency Public License, Version 2.0
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.ArgumentDescriptor;
import opssat.simulator.util.ArgumentTemplate;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.CommandResult;
import opssat.simulator.util.PlatformMessage;
import opssat.simulator.util.SimulatorData;
import opssat.simulator.util.SimulatorDeviceData;
import opssat.simulator.util.SimulatorHeader;
import opssat.simulator.util.SimulatorSchedulerPiece;

/**
 *
 * @author Cezar Suteu
 */
public class GuiMainWindow implements Runnable {

  private final String appFont = "Courier New";
  private final Color colorCommandsSent = Color.YELLOW;
  private final Color colorInputCommand = Color.BLUE;
  private final Color colorOutputOK = Color.GREEN;
  private final Color colorOutputKO = Color.RED;
  private final Color colorDescription = Color.WHITE;
  private final String pwd = System.getProperty("user.dir");
  private Properties platformProperties;
  private JSch ftp;
  private ChannelSftp sftpChannel;
  private GuiMainWindow window;

  JLabel lblSimulatorData;
  JLabel lblSimulatorTimeRunning;
  JTextArea txtConsole;
  JTextArea txtScheduler;
  JTextArea txtInputArguments;
  JTextArea txtOutputArguments;
  JFrame frame;
  JComboBox comboCommands;
  JComboBox comboTemplates;
  JCheckBox chkBoxShowAll;
  JLabel lblIFMethodDescriptor;
  JTextArea lblMethodDescriptor;
  JTextArea lblCommandResult;
  JCheckBox chkBok;
  private final static Integer INITIAL_WIDTH_PX = 1280;
  private final static Integer INITIAL_HEIGHT_PX = 720;
  private final static int CYCLES_TEST = 20;
  private static final int DEFAULT_NUM_TABS = 3;
  GuiApp parent;
  JPanel panelTop;
  JTabbedPane panelTabbed;
  JPanel panelMain;
  JPanel panelScheduler;
  JPanel panelLoader;
  JPanel panelBottom;
  JButton startStopButton;
  JButton pauseResumeButton;
  JTextField txtTimeFactor;
  int cyclesTest;
  boolean isConnected;
  boolean isEnduranceTest;
  String targetURL;
  int targetPort;
  SimulatorHeader simulatorHeader;
  GuiSimulatorHeaderEdit editForm;
  boolean templateChanged;

  Hashtable<String, WholeTextAreaObject> hashTableDataOutAgregate;

  LinkedList<CommandDescriptor> commandsList;

  Logger logger;
  private JTextField textFieldPath;
  private JTable tableCurrentSettings;

  public GuiMainWindow(final GuiApp parent, String targetURL, int targetPort) {
    this.parent = parent;
    logger = parent.getLogger();
    this.targetURL = targetURL;
    this.targetPort = targetPort;
    window = this;

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
        parent.startSocket();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public void setParent(GuiApp parent) {
    this.parent = parent;
  }

  public Logger getLogger() {
    return logger;
  }

  private void refreshPlatformProperties() {
    platformProperties = new Properties();
    parent.addGUIInteraction(platformProperties);
  }

  private CommandDescriptor getCommandDescriptorForID(int internalID) {
    for (CommandDescriptor c : commandsList) {
      if (c.getInternalID() == internalID) {
        return c;
      }
    }
    return null;
  }

  private ArgumentTemplate getArgTemplateForString(CommandDescriptor command,
      String templateDescription) {
    for (ArgumentTemplate t : command.getTemplateList()) {
      if (t.getDescription().equals(templateDescription)) {
        return t;
      }
    }
    return null;
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be invoked
   * from the event-dispatching thread.
   */
  private ImageIcon createImageIconFromBMPResource(String path) {
    // java.net.URL imgURL = GuiMainWindow.class.getClassLoader().getResource(path);
    /*
     * String workingdir = System.getProperty("user.dir"); File interfacesFile = new
     * File(workingdir, "src/main/resources/" + path); String targetPath = "";
     * targetPath = interfacesFile.getAbsolutePath();
     */
    String absolutePath = SimulatorNode.handleResourcePath(path, logger,
        getClass().getClassLoader(), false);
    File interfacesFile = new File(absolutePath);
    String targetPath = interfacesFile.getAbsolutePath();

    if (interfacesFile.exists()) {
      try {
        Image image = ImageIO.read(interfacesFile);
        ImageIcon result = new ImageIcon(image);
        return result;
      } catch (IOException ex) {
        Logger.getLogger(GuiMainWindow.class.getName()).log(Level.SEVERE, null, ex);
      }

      return null;
    } else {
      System.err.println("Couldn't find file: " + targetPath);
      return null;
    }

  }

  private void createTabsBasedOnDeviceList(LinkedList<SimulatorDeviceData> deviceList) {
    if (this.panelTabbed.getTabCount() == DEFAULT_NUM_TABS) {
      for (SimulatorDeviceData simulatorDeviceData : deviceList) {
        JPanel newPanel = new JPanel();
        JCheckBox checkBox = new JCheckBox("Update values");
        checkBox.setSelected(true);

        JButton copyToClipBoard = new JButton("Copy all to clipboard");
        JTextArea wholeField = new JTextArea();
        wholeField.setEditable(false);
        wholeField.setLineWrap(true);
        hashTableDataOutAgregate.put(simulatorDeviceData.getName(),
            new WholeTextAreaObject(wholeField, true));
        final Boolean updateValues = true;
        final String targetDevice = simulatorDeviceData.getName();
        checkBox.addItemListener(new ItemListener() {
          @Override
          public void itemStateChanged(ItemEvent e) {
            hashTableDataOutAgregate.get(targetDevice)
                .setUpdateValues(e.getStateChange() == ItemEvent.SELECTED);
            // updateValues = (e.getStateChange() == ItemEvent.SELECTED);
          }
        });

        copyToClipBoard.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(
                hashTableDataOutAgregate.get(targetDevice).getTextArea().getText());
            clpbrd.setContents(stringSelection, null);
          }
        });

        JPanel linePanel = new JPanel();
        JPanel wholeFieldPanel = new JPanel();
        BoxLayout lineLayout = new BoxLayout(linePanel, BoxLayout.X_AXIS);
        linePanel.add(checkBox);
        linePanel.add(copyToClipBoard);
        linePanel.setLayout(lineLayout);

        newPanel.add(wholeField);
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));

        String title = simulatorDeviceData.getName();
        DefaultCaret caretConsole = (DefaultCaret) wholeField.getCaret();
        caretConsole.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JScrollPane scrollPanel = new JScrollPane(newPanel);
        wholeFieldPanel.add(linePanel);
        wholeFieldPanel.add(scrollPanel);
        BoxLayout lineLayoutWhole = new BoxLayout(wholeFieldPanel, BoxLayout.Y_AXIS);
        wholeFieldPanel.setLayout(lineLayoutWhole);

        this.panelTabbed.addTab(title, wholeFieldPanel);
      }
    }
  }

  private void createAndShowGUI() {
    // Turn off metal's use of bold fonts
    UIManager.put("swing.boldMetal", Boolean.FALSE);
    UIManager.put("TitledBorder.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("Button.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("Label.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("CheckBox.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("ComboBox.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("TextArea.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("TextField.font", new Font(appFont, Font.PLAIN, 11));
    UIManager.put("TabbedPane.font", new Font(appFont, Font.PLAIN, 11));

    // Initializations

    hashTableDataOutAgregate = new Hashtable<String, WholeTextAreaObject>();
    this.frame = new JFrame("OPS-SAT Simulator");
    this.frame.setIconImage(createImageIconFromBMPResource("ESA-logo.png").getImage());
    this.lblSimulatorData = new JLabel("SimulatorData");
    this.lblSimulatorTimeRunning = new JLabel("TimeRunning");
    this.lblSimulatorTimeRunning.setFont(new Font(appFont, Font.BOLD, 14));
    this.pauseResumeButton = new JButton("Time Control");
    this.startStopButton = new JButton("System Control");
    JLabel lblTimeFactor = new JLabel("time factor x");
    JButton editHeaderButton = new JButton("Edit Header");
    JButton btnNewTemplate = new JButton("New");
    JButton btnSaveTemplate = new JButton("Save");
    JButton btnUpdateServer = new JButton("Update Server");
    JButton sendManualButton = new JButton("Send Manual Command");
    JButton resetInputArgsButton = new JButton("Reset Input Arguments");
    this.chkBoxShowAll = new JCheckBox("Show all");
    JCheckBox chkBoxPeriodicSending = new JCheckBox("Enable periodic sending");
    ImageIcon icon = createImageIconFromBMPResource("03_logo_dark_blue.bmp");
    JLabel lblESALogo = new JLabel(icon);
    JLabel lblTemplates = new JLabel("Input templates");
    final JTextField txtLoaderPrompt = new JTextField(targetURL + ":" + targetPort);
    this.panelLoader = new JPanel();
    this.txtConsole = new JTextArea();
    DefaultCaret caretConsole = (DefaultCaret) txtConsole.getCaret();
    caretConsole.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    JScrollPane scroll = new JScrollPane(txtConsole);
    this.txtScheduler = new JTextArea();
    DefaultCaret caretScheduler = (DefaultCaret) txtScheduler.getCaret();
    caretScheduler.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    JScrollPane scrollScheduler = new JScrollPane(txtScheduler);
    this.panelTop = new JPanel();
    this.panelTabbed = new JTabbedPane();
    panelTabbed
        .setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, new Color(204, 204, 204),
            new Color(0, 0, 0), null, null), new LineBorder(new Color(74, 74, 74), 1, true)));
    this.panelMain = new JPanel();
    this.panelTabbed.addTab("Manual Commands", this.panelMain);
    this.panelScheduler = new JPanel();
    this.panelTabbed.addTab("Scheduler", this.panelScheduler);
    // this.panelTabbed.addTab("Test",new JPanel());
    JPanel panelManualMethods = new JPanel();
    this.comboCommands = new JComboBox();
    this.comboTemplates = new JComboBox();
    this.lblIFMethodDescriptor = new JLabel();
    this.lblMethodDescriptor = new JTextArea();
    JScrollPane scrollLblMethodDescriptor = new JScrollPane(lblMethodDescriptor);
    this.lblCommandResult = new JTextArea();
    this.txtInputArguments = new JTextArea("[Input arguments]");
    JScrollPane scrollTxtInputArguments = new JScrollPane(txtInputArguments);
    this.txtOutputArguments = new JTextArea("[Output]");
    JScrollPane scrollTxtOutputArguments = new JScrollPane(txtOutputArguments);
    JScrollPane scrollLblCommandResult = new JScrollPane(lblCommandResult);
    this.panelBottom = new JPanel();
    txtTimeFactor = new JTextField("2");

    this.frame.setSize(new Dimension(INITIAL_WIDTH_PX, INITIAL_HEIGHT_PX));
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    txtTimeFactor.setPreferredSize(new Dimension(35, 20));
    txtTimeFactor.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String newFactor = txtTimeFactor.getText();
        int newTimeFactor = Integer.parseInt(newFactor);
        if (newTimeFactor < 1) {
          newTimeFactor = 1;
        }
        if (newTimeFactor > 1000) {
          newTimeFactor = 1000;
        }
        parent.addGUIInteraction("TimeFactor:" + String.valueOf(newTimeFactor));
      }
    });
    chkBoxShowAll.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        putManualCommandsInCombo(e.getStateChange() == ItemEvent.SELECTED);
      }
    });
    chkBoxPeriodicSending.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        isEnduranceTest = (e.getStateChange() == ItemEvent.SELECTED);
      }
    });
    startStopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        parent.addGUIInteraction("ToggleStartStop");
      }
    });
    editHeaderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editForm = new GuiSimulatorHeaderEdit(simulatorHeader, GuiMainWindow.this);
      }
    });

    pauseResumeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        parent.addGUIInteraction("TogglePauseResume");
      }
    });

    sendManualButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object c = comboCommands.getSelectedItem();
        if (c instanceof CommandDescriptor) {
          sendManualCommand((CommandDescriptor) c, true);
        }
      }
    });
    resetInputArgsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object c = comboCommands.getSelectedItem();
        if (c instanceof CommandDescriptor) {
          ((CommandDescriptor) c).resetInputArgs();
          displayManualMethod(((CommandDescriptor) c));
        }
      }
    });

    btnNewTemplate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String test1 = JOptionPane.showInputDialog("Input description for new template: ");
        Object c = comboCommands.getSelectedItem();
        if (test1 != null && c instanceof CommandDescriptor) {
          if (test1.length() == 0) {
            JOptionPane.showMessageDialog(frame, "The description cannot be empty");
          } else if (!test1.matches("^[a-zA-Z0-9]*$")) {
            JOptionPane.showMessageDialog(frame, "Template name must be alphanumeric.");
          } else if (((CommandDescriptor) c).addNewEmptyTemplate(test1)) {
            displayManualMethod(((CommandDescriptor) c));
            showMessageConsole("User;Local;AddNewTemplate;"
                + ((CommandDescriptor) c).getMethodBody() + "" + test1);
          } else {
            JOptionPane.showMessageDialog(frame, "The description entered is not unique");
          }
        }
      }
    });
    btnSaveTemplate.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // String test1= JOptionPane.showInputDialog("Input description for new
        // template: ");
        Object c = comboCommands.getSelectedItem();
        String cTemplateDescription = (String) comboTemplates.getSelectedItem();
        if (c instanceof CommandDescriptor) {
          boolean dataOk = sendManualCommand((CommandDescriptor) c, false);
          if (dataOk) {
            if (cTemplateDescription.equals(CommandDescriptor.KEYWORD_DEFAULT)) {
              JOptionPane.showMessageDialog(frame,
                  "Default template edit cannot be saved, create a new one or edit another one.");
            } else if (((CommandDescriptor) c).updateTemplate(cTemplateDescription,
                txtInputArguments.getText())) {
              templateChanged = false;
            } else {
              JOptionPane.showMessageDialog(frame,
                  "Could not find template [" + cTemplateDescription + "] in command ["
                      + ((CommandDescriptor) c).getMethodBody() + "]!");
            }
          }
        }
      }
    });
    btnUpdateServer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int resultConfirm = JOptionPane.showConfirmDialog(frame,
            "Server will be updated with local copy of commands & templates. Do you want to continue?",
            "Update server", JOptionPane.YES_NO_OPTION);
        if (resultConfirm == JOptionPane.YES_OPTION) {
          showMessageConsole("User;Local;UpdateServer;");
          LinkedList<CommandDescriptor> newCommandsList = new LinkedList<CommandDescriptor>();
          for (int i = 0; i < commandsList.size(); i++) {
            newCommandsList.add(commandsList.get(i));
          }
          parent.addGUIInteraction(newCommandsList);
        } else {
          ;
        }
      }
    });

    txtLoaderPrompt.setBackground(Color.orange);

    txtLoaderPrompt.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        String targetConnection = txtLoaderPrompt.getText();
        List<String> items = Arrays.asList(targetConnection.split(":"));
        boolean dataOk = items.size() == 2;
        if (dataOk) {
          targetURL = items.get(0);
          targetPort = Integer.parseInt(items.get(1));
          parent.setTargetConnection(targetURL, targetPort);
          // SFTP
          SFTPInformation ui = new SFTPInformation(GuiMainWindow.this);
          ui.setBounds(100, 100, 496, 145);
          ui.setVisible(true);
          GuiMainWindow.this.frame.setEnabled(false);
        } else {
          txtLoaderPrompt.setText(targetURL + ":" + targetPort);
        }

      }
    });

    this.panelLoader.setLayout(new BoxLayout(panelLoader, BoxLayout.Y_AXIS));
    lblESALogo.setAlignmentX(Component.CENTER_ALIGNMENT);
    txtLoaderPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
    txtLoaderPrompt.setHorizontalAlignment(JTextField.CENTER);
    this.panelLoader.add(lblESALogo);
    this.panelLoader.add(txtLoaderPrompt);

    txtConsole.setEditable(false);
    txtConsole.setBackground(Color.DARK_GRAY);
    txtConsole.setForeground(Color.GREEN);

    fixScrollPaneForMain(scroll);
    // fixScrollPaneForMain(scrollScheduler);

    panelTop.setBorder(new TitledBorder(new EtchedBorder(), "Simulator Core Control"));

    // panelMain.setBorder(new TitledBorder(new EtchedBorder(), "Manual Commands"));
    comboCommands.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
    // chkCommands.setPrototypeDisplayValue(" ");
    comboCommands.addItem("Select");
    comboCommands.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          Object item = arg0.getItem();
          if (item instanceof CommandDescriptor) {
            displayManualMethod(((CommandDescriptor) item));
          }
        }
      }
    });
    comboTemplates.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
          Object item = arg0.getItem();
          Object commandDescriptorItem = comboCommands.getItemAt(comboCommands.getSelectedIndex());
          if (item instanceof String && commandDescriptorItem instanceof CommandDescriptor) {
            logger.log(Level.FINE, "Looking up " + ((String) item));
            if (((CommandDescriptor) commandDescriptorItem).templateSelected((String) item)) {
              displayManualMethodTemplate(((CommandDescriptor) commandDescriptorItem));
            } else {
              JOptionPane.showMessageDialog(frame, "Unable to select input template");
            }
          }
        }
      }
    });

    this.lblMethodDescriptor.setEditable(false);
    DefaultCaret caret = (DefaultCaret) lblMethodDescriptor.getCaret();
    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

    fixScrollPaneForMain(scrollLblMethodDescriptor);

    this.lblCommandResult.setEditable(false);
    this.lblCommandResult.setBackground(colorDescription);
    this.lblMethodDescriptor.setText("[Command Description]");
    this.lblMethodDescriptor.setBackground(colorDescription);

    this.txtInputArguments.setBackground(Color.BLUE);
    this.txtInputArguments.setForeground(Color.WHITE);

    fixScrollPaneForMain(scrollTxtInputArguments);

    this.txtOutputArguments.setEditable(false);
    this.txtOutputArguments.setLineWrap(true);

    fixScrollPaneForMain(scrollTxtOutputArguments);

    this.lblCommandResult.setText("[Command Result]");
    this.lblCommandResult.setLineWrap(true);

    fixScrollPaneForMain(scrollLblCommandResult);
    txtOutputArguments.setBackground(colorDescription);

    panelScheduler.setLayout(new BoxLayout(panelScheduler, BoxLayout.Y_AXIS));
    panelScheduler.setBorder(new TitledBorder(new EtchedBorder(), "Scheduler output"));
    panelBottom.setLayout(new BoxLayout(panelBottom, BoxLayout.Y_AXIS));
    panelBottom.setBorder(new TitledBorder(new EtchedBorder(), "Console"));

    panelTop.add(lblSimulatorTimeRunning);
    panelTop.add(startStopButton);
    panelTop.add(pauseResumeButton);
    panelTop.add(lblTimeFactor);
    panelTop.add(txtTimeFactor);
    panelTop.add(lblSimulatorData);
    panelTop.add(editHeaderButton);

    panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

    panelMain.add(comboCommands);

    sendManualButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    resetInputArgsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    panelManualMethods.add(chkBoxShowAll);
    panelManualMethods.add(sendManualButton);
    panelManualMethods.add(resetInputArgsButton);
    panelManualMethods.add(lblTemplates);
    panelManualMethods.add(comboTemplates);
    panelManualMethods.add(btnNewTemplate);
    panelManualMethods.add(btnSaveTemplate);
    panelManualMethods.add(btnUpdateServer);
    panelManualMethods.add(chkBoxPeriodicSending);

    panelMain.add(panelManualMethods);
    panelMain.add(scrollLblMethodDescriptor);
    panelMain.add(scrollTxtInputArguments);
    panelMain.add(scrollTxtOutputArguments);
    panelMain.add(scrollLblCommandResult);

    panelBottom.add(scroll);
    panelScheduler.add(scrollScheduler);

    final JPanel panelCameraSettings = new JPanel();
    panelTabbed.addTab("Camera Simulator Settings", null, panelCameraSettings, null);

    JLabel lblMode = new JLabel("Image selection mode:");

    final JComboBox<String> selectMode = new JComboBox<String>();
    selectMode.setModel(new DefaultComboBoxModel<String>(new String[] { "Fixed", "Random" }));
    selectMode.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
        textFieldPath.setText("");
      }
    });
    SpringLayout sl_panelCameraSettings = new SpringLayout();
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, lblMode, 10, SpringLayout.NORTH,
        panelCameraSettings);
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, selectMode, -4, SpringLayout.NORTH,
        lblMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, selectMode, 10, SpringLayout.EAST,
        lblMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.EAST, selectMode, 120, SpringLayout.EAST,
        lblMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, lblMode, 5, SpringLayout.WEST,
        panelCameraSettings);
    panelCameraSettings.setLayout(sl_panelCameraSettings);
    panelCameraSettings.add(lblMode);
    panelCameraSettings.add(selectMode);

    textFieldPath = new JTextField();
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, textFieldPath, 5, SpringLayout.SOUTH,
        selectMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, textFieldPath, 0, SpringLayout.WEST,
        lblMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.EAST, textFieldPath, 0, SpringLayout.EAST,
        selectMode);
    panelCameraSettings.add(textFieldPath);
    textFieldPath.setColumns(10);

    JButton btnOpenTargetSelect = new JButton("Browse");
    btnOpenTargetSelect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (targetURL.equals("127.0.0.1")) { // browse locally
          JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
          if (selectMode.getSelectedItem().equals("Fixed")) {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.removeChoosableFileFilter(fc.getChoosableFileFilters()[0]);
            fc.setFileFilter(
                new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp", "raw"));
          } else {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          }
          int res = fc.showOpenDialog(panelCameraSettings);
          if (res == JFileChooser.APPROVE_OPTION) {
            String chosenName = fc.getSelectedFile().getAbsolutePath();
            textFieldPath.setText(chosenName);
          }
        } else { // use SFTP browser
          SFTPBrowser browser = new SFTPBrowser(sftpChannel, (String) selectMode.getSelectedItem(),
              window);
          browser.setBounds(100, 100, 400, 300);
          browser.setVisible(true);
          frame.setEnabled(false);
        }
      }
    });
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, btnOpenTargetSelect, 0,
        SpringLayout.NORTH, textFieldPath);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, btnOpenTargetSelect, 5,
        SpringLayout.EAST, selectMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.SOUTH, btnOpenTargetSelect, 0,
        SpringLayout.SOUTH, textFieldPath);
    panelCameraSettings.add(btnOpenTargetSelect);

    JButton btnApplyCamSettings = new JButton("Apply");
    btnApplyCamSettings.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        ArrayList<PlatformMessage> updates = new ArrayList<PlatformMessage>();
        String path = textFieldPath.getText();
        String mode = (String) selectMode.getSelectedItem();
        updates.add(new PlatformMessage("camerasim.imagemode", mode));
        if (!path.equals("")) {
          if (mode.equals("Fixed")) {
            updates.add(new PlatformMessage("camerasim.imagefile", path));
          } else if (mode.equals("Random")) {
            updates.add(new PlatformMessage("camerasim.imagedirectory", path));
          }
        }
        // Send updates to server
        for (PlatformMessage p : updates) {
          parent.addGUIInteraction(p);
        }

        parent.getToServerQueue().add("refreshConfig");
        GuiMainWindow.this.refreshPlatformProperties();
      }
    });
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, btnApplyCamSettings, 5,
        SpringLayout.SOUTH, textFieldPath);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, btnApplyCamSettings, 0,
        SpringLayout.WEST, lblMode);
    panelCameraSettings.add(btnApplyCamSettings);

    TableColumnModel columnTitles = new DefaultTableColumnModel();
    TableColumn prop = new TableColumn();
    prop.setHeaderValue("Property");
    columnTitles.addColumn(prop);
    TableColumn val = new TableColumn();
    val.setHeaderValue("Value");
    columnTitles.addColumn(val);

    JLabel lblTitleSettings = new JLabel("Current Camera Settings");
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, lblTitleSettings, 0,
        SpringLayout.NORTH, lblMode);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, lblTitleSettings, 15, SpringLayout.EAST,
        btnOpenTargetSelect);
    panelCameraSettings.add(lblTitleSettings);

    JSeparator separator = new JSeparator();
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, separator, 0, SpringLayout.NORTH,
        lblTitleSettings);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.EAST,
        btnOpenTargetSelect);
    sl_panelCameraSettings.putConstraint(SpringLayout.SOUTH, separator, -10, SpringLayout.SOUTH,
        panelCameraSettings);
    separator.setForeground(new Color(128, 128, 128));
    separator.setOrientation(SwingConstants.VERTICAL);
    panelCameraSettings.add(separator);

    tableCurrentSettings = new JTable() {
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
    tableCurrentSettings
        .setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Property", "Value" }) {
          Class[] columnTypes = new Class[] { String.class, String.class };

          @Override
          public Class getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
          }

          boolean[] columnEditables = new boolean[] { false, false };

          @Override
          public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
          }
        });
    tableCurrentSettings.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // tableCurrentSettings.getColumnModel().getColumn(0).setMinWidth(180);
    // tableCurrentSettings.getColumnModel().getColumn(1).setMinWidth(1500);
    JScrollPane scrollPane = new JScrollPane(tableCurrentSettings);
    sl_panelCameraSettings.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH,
        textFieldPath);
    sl_panelCameraSettings.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST,
        lblTitleSettings);
    sl_panelCameraSettings.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH,
        panelCameraSettings);
    sl_panelCameraSettings.putConstraint(SpringLayout.EAST, scrollPane, -30, SpringLayout.EAST,
        panelCameraSettings);
    tableCurrentSettings.setFillsViewportHeight(true);

    panelCameraSettings.add(scrollPane);
    // scrollPane.setRowHeaderView(tableCurrentSettings);
    this.frame.setVisible(true);
    this.showMessageConsole("Initialized.");

    if (!this.isConnected) {
      showConnectedInfo(false);
    }
  }

  /**
   * Sets the textfield of the camera settings tab (invoked from SFTPBrowser).
   * 
   * @param path The path to put into the text field.
   */
  public void setPathFromSFTP(String path) {
    textFieldPath.setText(path);
    this.frame.setEnabled(true);
  }

  /**
   * Connects the SFTP client to the server
   * 
   * @param ui SFTPInformation containing important connection information.
   * @return true on success, false otherwise
   */
  public boolean connectStfp(SFTPInformation ui) {
    ftp = new JSch();
    try {
      ftp.setKnownHosts(ui.getKnownHosts());
      Session session = ftp.getSession(ui.getUsername(), targetURL, 22);
      session.setUserInfo(ui);
      session.connect();
      Channel channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;
      refreshPlatformProperties();
      this.frame.setEnabled(true);
      return true;
    } catch (JSchException e1) {
      e1.printStackTrace();
      JOptionPane.showMessageDialog(this.frame,
          "Failed to init connection. Check your username and password!");
      SFTPInformation nui = new SFTPInformation(this);
      nui.setBounds(100, 100, 500, 300);
      nui.setVisible(true);
      this.frame.setEnabled(false);
      return false;
      // this.frame.dispose();
    }
  }

  /**
   * Reloads the camera properties in the table
   */
  public void updateCamSettingsTable() {
    while (platformProperties.size() == 0) {
      // NOP
    }
    DefaultTableModel dtm = (DefaultTableModel) tableCurrentSettings.getModel();
    dtm.setRowCount(0); // clear table
    Set<Entry<Object, Object>> props = platformProperties.entrySet();
    for (Entry<Object, Object> e : props) {
      String key = (String) e.getKey();
      if (key.startsWith("camera")) {
        String val = (String) e.getValue();
        dtm.addRow(new String[] { key, val });
      }
    }
  }

  public boolean sendManualCommand(CommandDescriptor c, boolean doRun) {
    // data = data.replaceAll("\\{.*?}", "");

    String test = txtInputArguments.getText().replaceAll("\\s*\\([^\\)]*\\)\\s*", "");
    this.logger.log(Level.INFO, test);
    final String dataOk = c.setInputArgsFromString(test);
    if (dataOk.equals("ParseOk")) {
      if (doRun) {
        parent.addGUIInteraction(c);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            txtInputArguments.setBackground(colorCommandsSent);
            txtInputArguments.setForeground(Color.BLACK);
            txtInputArguments.setCaretColor(Color.BLACK);
            txtOutputArguments.setText("Waiting for result..");
            lblCommandResult.setText("");
            txtOutputArguments.setBackground(Color.LIGHT_GRAY);
          }
        });
      }
      return true;
    } else {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txtInputArguments.setBackground(Color.RED);
          txtInputArguments.setForeground(Color.WHITE);
          txtInputArguments.setCaretColor(Color.WHITE);
          lblCommandResult.setText(dataOk);
          txtOutputArguments.setText("");
          txtOutputArguments.setBackground(Color.LIGHT_GRAY);
        }
      });
      return false;
    }
  }

  private void handleSchedulerList(final LinkedList<SimulatorSchedulerPiece> data) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        txtScheduler.setText("");
        String schedulerDataStr = "";
        for (SimulatorSchedulerPiece piece : data) {
          CommandDescriptor c = getCommandDescriptorForID(piece.getInternalID());
          ArgumentTemplate t = getArgTemplateForString(c, piece.getArgumentTemplateDescription());
          schedulerDataStr = schedulerDataStr + piece.getSchedulerOutput() + c.getMethodBody()
              + CommandDescriptor.SEPARATOR_DATAFILES + t.getArgContent() + "\n";
        }
        txtScheduler.setText(schedulerDataStr);
      }
    });
  }

  public void showMessageConsole(final String data) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd;HH:mm:ss");
        Date date = new Date();
        txtConsole.append(dateFormat.format(date) + ";" + data + "\n");
        while (txtConsole.getLineCount() > 500) {
          try {
            int end = txtConsole.getLineEndOffset(0);
            txtConsole.replaceRange("", 0, end);
          } catch (BadLocationException ex) {
            Logger.getLogger(GuiMainWindow.class.getName()).log(Level.SEVERE, null, ex);
          }
        }

      }
    });

  }

  public String getKeyFromDeviceDataAndPiece(SimulatorDeviceData deviceData,
      ArgumentDescriptor devicePiece) {
    return deviceData.getName() + devicePiece.getName();
  }

  public void showSimulatorDeviceData(
      final LinkedList<SimulatorDeviceData> linkedSimulatorDeviceData) {
    this.logger.log(Level.ALL, "Received list of SimulatorDeviceData with ["
        + linkedSimulatorDeviceData.size() + "] items");
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        for (SimulatorDeviceData simulatorDeviceData : linkedSimulatorDeviceData) {
          if (hashTableDataOutAgregate.get(simulatorDeviceData.getName()).isUpdateValues()) {
            String composite = "";
            for (ArgumentDescriptor simulatorDeviceDataPiece : simulatorDeviceData.getDataList()) {
              composite = composite + simulatorDeviceDataPiece.toString() + "\n\n";
            }
            hashTableDataOutAgregate.get(simulatorDeviceData.getName()).getTextArea()
                .setText(composite);
          }
        }
      }
    });
  }

  public void showCommandResult(final CommandResult data) {

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblCommandResult.setText(data.toExtString());
        Object obj = data.getOutput();
        txtOutputArguments.setText(data.getOutputAsString());
        txtOutputArguments.setBackground(data.isCommandFailed() ? colorOutputKO : colorOutputOK);
        txtInputArguments.setBackground(colorInputCommand);
        txtInputArguments.setForeground(Color.WHITE);
        txtInputArguments.setCaretColor(Color.WHITE);
      }
    });

  }

  public void displayManualMethod(final CommandDescriptor data) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblMethodDescriptor.setText(data.getComment());
        displayManualMethodTemplate(data);
        lblCommandResult.setText("[Output]");
        txtOutputArguments.setText("[Result]");
        txtInputArguments.setBackground(colorInputCommand);
        txtInputArguments.setCaretColor(Color.WHITE);
        txtOutputArguments.setBackground(Color.LIGHT_GRAY);
        comboTemplates.removeAllItems();
        for (ArgumentTemplate t : data.getTemplateList()) {
          comboTemplates.addItem(t.getDescription());
        }
        if (isEnduranceTest) {
          sendManualCommand(data, true);
        }
      }
    });

  }

  public void displayManualMethodTemplate(final CommandDescriptor data) {
    txtInputArguments.setLineWrap(true);
    txtInputArguments.setText(data.getInputArgs());
  }

  private void clearManualCommands() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        comboCommands.removeAllItems();
      }
    });
  }

  private void addManualCommandsItem(final CommandDescriptor data) {

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        comboCommands.addItem(data);

      }
    });
  }

  public void editHeaderReady() {
    this.editForm.makeFormColor(Color.yellow);
    parent.addGUIInteraction(simulatorHeader);
  }

  private void fixScrollPaneForMain(JScrollPane pane) {
    pane.setMinimumSize(new Dimension(Integer.MAX_VALUE, 50));
    pane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));
    pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
  }

  public void processSimulatorData(final SimulatorData data) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        lblSimulatorData.setText(data.toString());
        Map<TimeUnit, Long> computedDiff;
        if (simulatorHeader != null) {
          computedDiff = SimulatorData.computeDiff(simulatorHeader.getStartDate(),
              data.getCurrentTime());
          String localMinutes = String.format(simulatorHeader.FROM_START_FORMAT,
              computedDiff.get(TimeUnit.DAYS), computedDiff.get(TimeUnit.HOURS),
              computedDiff.get(TimeUnit.MINUTES), computedDiff.get(TimeUnit.SECONDS),
              computedDiff.get(TimeUnit.MILLISECONDS));
          lblSimulatorTimeRunning.setText(localMinutes.toString());
        }
        if (!txtTimeFactor.isFocusOwner()
            && !txtTimeFactor.getText().equals(String.valueOf(data.getTimeFactor()))) {
          txtTimeFactor.setText(String.valueOf(data.getTimeFactor()));
        }
        if (data.isTimeRunning()) {
          pauseResumeButton.setText("Time");
          pauseResumeButton.setBackground(Color.GREEN);
        } else {
          pauseResumeButton.setText("Time");
          pauseResumeButton.setBackground(Color.RED);
        }
        if (data.isSimulatorRunning()) {
          startStopButton.setText("Enable");
          startStopButton.setBackground(Color.GREEN);

        } else {
          startStopButton.setText("Enable");
          startStopButton.setBackground(Color.RED);
        }
      }
    });

  }

  public void makeEnduranceTest() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int newIndex = comboCommands.getSelectedIndex();
        newIndex++;
        if (newIndex >= comboCommands.getItemCount()) {
          newIndex = 0;
        }
        if (comboCommands.getItemCount() == 1) {
          sendManualCommand((CommandDescriptor) comboCommands.getSelectedItem(), true);
        } else {
          comboCommands.setSelectedIndex(newIndex);
        }
      }
    });
  }

  public void showConnectedInfo(final boolean isConnected) {
    this.isConnected = isConnected;
    this.showMessageConsole("showConnectedInfo [" + isConnected + "].");
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();
        if (!isConnected) {
          clearManualCommands();
          frame.getContentPane().add(panelLoader);
        } else {
          frame.getContentPane().add(panelTop, BorderLayout.NORTH);
          ;
          frame.getContentPane().add(panelTabbed);
          GuiMainWindow.this.refreshPlatformProperties();
        }
        frame.getContentPane().add(panelBottom, BorderLayout.PAGE_END);
        frame.getContentPane().validate();

      }
    });
  }

  public void putManualCommandsInCombo(final boolean showAll) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        chkBoxShowAll.setSelected(showAll);

      }
    });
    clearManualCommands();
    for (CommandDescriptor c : commandsList) {
      if (c.isVisible() || showAll) {
        addManualCommandsItem(c);
      }
    }
  }

  @Override
  public void run() {
    while (true) {

      try {
        String preamble = "FromServer";
        Object result = this.parent.getFromServerQueue().poll();
        if (result instanceof CommandResult) {
          CommandResult convertedResult = (CommandResult) result;
          showMessageConsole(preamble + ";Result;" + convertedResult.toString());
          showCommandResult(convertedResult);
        } else if (result instanceof SimulatorHeader) {
          SimulatorHeader convertedResult = (SimulatorHeader) result;
          showMessageConsole(preamble + ";Result;" + convertedResult.toString());
          simulatorHeader = ((SimulatorHeader) result);
          if (editForm != null) {
            editForm.makeFormColor(Color.green);
            editForm.dispose();
          }
        } else if (result instanceof Properties) {
          this.platformProperties = (Properties) result;
          this.updateCamSettingsTable();
        } else if (result instanceof SimulatorData) {
          processSimulatorData((SimulatorData) result);
        } else if (result instanceof String) {
          String res = (String) result;
          if (res.contains("PWD")) {
            String[] pwd = res.split(":");
            this.parent.setSimulatorWorkingDir(pwd[1]);
          }
          showMessageConsole(res);
        } else if (result instanceof LinkedList) {
          LinkedList linkedListResult = (LinkedList) result;
          Object testItem = null;
          try {
            testItem = ((LinkedList) result).get(0);
          } catch (IndexOutOfBoundsException ex) {
            continue;
          }
          // System.out.println(testItem.getClass().getName());
          if (testItem instanceof SimulatorSchedulerPiece) {
            // System.out.println("LinkedList<SimulatorSchedulerPiece> received");
            handleSchedulerList((LinkedList<SimulatorSchedulerPiece>) result);
          } else if (testItem instanceof CommandDescriptor) {

            commandsList = new LinkedList<CommandDescriptor>();// (LinkedList<CommandDescriptor>)
                                                               // result;
            int obj = 0;
            int visibleItems = 0;
            while (linkedListResult.get(obj) instanceof CommandDescriptor) {
              CommandDescriptor commandDescriptor = (CommandDescriptor) linkedListResult.get(obj);
              commandDescriptor.setLogger(this.logger);
              commandsList.add(commandDescriptor);
              obj++;
              if (commandDescriptor.isVisible()) {
                visibleItems++;
              }
            }
            if (visibleItems > 0) {
              putManualCommandsInCombo(false);
            } else {
              putManualCommandsInCombo(true);
            }
            showMessageConsole(
                preamble + ";Received commands list with [" + commandsList.size() + "] methods");
            LinkedList<SimulatorDeviceData> devicesList = new LinkedList<SimulatorDeviceData>();// (LinkedList<CommandDescriptor>)
                                                                                                // result;
            while (obj < linkedListResult.size()
                && linkedListResult.get(obj) instanceof SimulatorDeviceData) {
              devicesList.add((SimulatorDeviceData) linkedListResult.get(obj));
              obj++;
            }
            createTabsBasedOnDeviceList(devicesList);
            showMessageConsole(preamble + ";Received simulator device data list with ["
                + devicesList.size() + "] methods");
            showConnectedInfo(true);
          } else if (testItem instanceof SimulatorDeviceData) {
            showSimulatorDeviceData(linkedListResult);
          }
        }
        if (--cyclesTest < 0 && isConnected && isEnduranceTest) {
          cyclesTest = CYCLES_TEST;
          makeEnduranceTest();
        }
        Thread.sleep(250);
      } catch (InterruptedException ex) {
        Logger.getLogger(GuiMainWindow.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
