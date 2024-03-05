package gui;

import gui.inter.ToolBarListner;
import gui.inter.filefilter.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Toolbar extends JToolBar implements ActionListener {
    private JButton saveButton;
    private JButton RefreshButton;
    private ToolBarListner textListner;
    private TextPanel textPanel;

    public Toolbar() {

        //Get rid of the border if you want the toolbar draggable.
        setBorder(BorderFactory.createEtchedBorder());

        //setFloatable(false);

        saveButton = new JButton();
        saveButton.setIcon(Utils.createIcon("/images/Save16.gif"));
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(this);

        RefreshButton = new JButton();
        RefreshButton.addActionListener(this);
        RefreshButton.setToolTipText("Refresh");
        RefreshButton.setIcon(Utils.createIcon("/images/Refresh16.gif"));

        //setLayout(new FlowLayout(FlowLayout.LEFT));
        add(saveButton);
        //addSeparator();
        add(RefreshButton);
    }

    public void setToolBarListner(ToolBarListner listner) {
        this.textListner = listner;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton) e.getSource();
        if (clicked == saveButton) {
            //textPanel.appendText("Hello\n");
            if (textListner != null) {
                textListner.saveEventOccured();
            }
        } else if (clicked == RefreshButton) {
            //textPanel.appendText("GoodBye\n");
            if (textListner != null) {
                textListner.refreshEventOccured();
            }
        }
    }
}
