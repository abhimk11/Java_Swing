package gui;

import controller.Controller;
import gui.inter.FormListner;
import gui.inter.ToolBarListner;
import gui.inter.event.FormEvent;
import gui.inter.filefilter.PersonFileFilter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    //private JTextArea textArea;

    private JButton btn;
    private Toolbar toolbar;
    private FormPanel formPanel;
    private JFileChooser fileChooser;
    private Controller controller;
    private TablePanel tablePanel;
    private PrefsDialog prefsDialog;
    private Preferences prefs;
    private JSplitPane splitPane;
    private JTabbedPane tabPane;
    private MessagePanel messagePanel;

    public MainFrame() {
        super("Hello World");

        setLayout(new BorderLayout());

        //textArea=new JTextArea();
        //btn=new JButton("Click me"); //component
        //textPanel = new TextPanel(); //component
        toolbar = new Toolbar();
        formPanel = new FormPanel();
        tablePanel = new TablePanel();
        prefsDialog = new PrefsDialog(this);
        tabPane = new JTabbedPane();
        messagePanel = new MessagePanel(this);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tabPane);

        splitPane.setOneTouchExpandable(true);

        tabPane.addTab("Person Database", tablePanel);
        tabPane.addTab("Messages", messagePanel);

        prefs = Preferences.userRoot().node("db");

        controller = new Controller();

        tablePanel.setData(controller.getPeople());

        tablePanel.setPersonTableListner(new PersonTableListner() {
            public void rowDeleted(int row) {
                controller.removePerson(row);
            }
        });

        tabPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int tabIndex = tabPane.getSelectedIndex();

                if (tabIndex==1){
                    messagePanel.refresh();
                }
            }
        });

        prefsDialog.setPrefsListner(new PrefsListner() {
            @Override
            public void preferencesSet(String user, String password, int port) {
                //System.out.println(user + ": "+ password);
                prefs.put("user", user);
                prefs.put("password", password);
                prefs.putInt("port", port);

                try {
                    controller.configure(port,user,password);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,"Unable to re-connect");
                }
            }
        });

        String user = prefs.get("user", "");
        String password = prefs.get("password", "");
        Integer port = prefs.getInt("port", 3306);

        prefsDialog.setDefaults(user, password, port);

        try {
            controller.configure(port,user,password);
        } catch (Exception e) {
            System.out.println("Can't connect to database");
        }

        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new PersonFileFilter());

        setJMenuBar(createMenuBar());

        toolbar.setToolBarListner(new ToolBarListner() {

            @Override
            public void saveEventOccured() {
                connect();
                try {
                    controller.save();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Unable to save to database.", "Database Connection Problem.", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void refreshEventOccured() {
                connect();
                try {
                    controller.load();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Unable to load from database.", "Database Connection Problem.", JOptionPane.ERROR_MESSAGE);
                }
                tablePanel.refresh();
            }
        });

        formPanel.setFormListner(new FormListner() {
            public void formEventOccurred(FormEvent e) {
                String name = e.getName();
                String occupation = e.getOccupation();
                int ageCat = e.getAgeCategory();
                String empCat = e.getEmpCat();

                //String taxId = e.getTaxId();
                //boolean usCitizen = e.isUsCitizen();
                String gender = e.getGender();
                //textPanel.appendText(name + ": " + occupation + " : " + ageCat + ": " + empCat + ": " + gender + "\n");

                controller.addPerson(e);
                tablePanel.refresh();
            }
        });

//        btn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) { //ByClicking BTN
//                //textArea.append("HELLO\n");
//                textPanel.appendText("Hello\n");
//            }
//        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.disconnect();
                //System.out.println("Window Closing");
                dispose();
                System.gc();
            }
        });

        //add(formPanel, BorderLayout.WEST);
        add(toolbar, BorderLayout.PAGE_START);
        //add(textPanel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.CENTER);
        //add(tablePanel, BorderLayout.CENTER);
        //add(btn,BorderLayout.SOUTH);

        setMinimumSize(new Dimension(500, 400));
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    private void connect() {
        try {
            controller.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainFrame.this, "Cannot connect to database.", "Database Connection Problem.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu windowMenu = new JMenu("Window");


        JMenuItem exportDataItem = new JMenuItem("Export Data...");
        JMenuItem importDataItem = new JMenuItem("Import Data...");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(exportDataItem);
        fileMenu.add(importDataItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu showMenu = new JMenu("Show");
        JMenuItem prefsItem = new JMenuItem("Preferences...");

        JMenuItem showFormItem = new JCheckBoxMenuItem("Person Form");
        showFormItem.setSelected(true);

        showMenu.add(showFormItem);
        windowMenu.add(showMenu);
        windowMenu.add(prefsItem);

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);

        prefsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prefsDialog.setVisible(true);
            }
        });

        showFormItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();

                if (menuItem.isSelected()) {
                    splitPane.setDividerLocation((int) formPanel.getMinimumSize().getWidth());
                }
                formPanel.setVisible(menuItem.isSelected());
            }
        });

        fileMenu.setMnemonic(KeyEvent.VK_F);
        exitItem.setMnemonic(KeyEvent.VK_X); //Mnemonic for accessibility using keys like alt+X or alt+firstUnderscore

        prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK)); //accerator like Ctrl+X for exit without Using MENU

        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK)); //accerator like Ctrl+X for exit without Using MENU

        importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK)); //accerator like Ctrl+X for exit without Using MENU

        importDataItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) ;

                try {
                    controller.loadFromFile(fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                //{System.out.println(fileChooser.getSelectedFile());}
            }
        });

        exportDataItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) ;

                try {
                    controller.saveToFile(fileChooser.getSelectedFile());
                    tablePanel.refresh();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Could not save data to file.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                {
                    System.out.println(fileChooser.getSelectedFile());
                }
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = JOptionPane.showInputDialog(MainFrame.this,
                        "Enter Your user name"
                        , "Enter User Name"
                        , JOptionPane.OK_OPTION | JOptionPane.QUESTION_MESSAGE);

                System.out.println(text);

                int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do you really want to exit the application"
                        , "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    WindowListener[] listeners = getWindowListeners();

                    for (WindowListener listener : listeners) {
                        listener.windowClosing(new WindowEvent(MainFrame.this, 0));
                    }
                }

            }

        });
        return menuBar;
    }

}
