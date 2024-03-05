package gui;

import model.EmploymentCategory;
import model.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TablePanel extends JPanel {

    private JTable table;
    private PersonTableModel tableModel;
    private JPopupMenu popup;
    private PersonTableListner personTableListner;

    public TablePanel() {
        tableModel = new PersonTableModel();
        table = new JTable(tableModel);
        popup = new JPopupMenu();

        table.setDefaultRenderer(EmploymentCategory.class,new EmploymentCategoryRenderer());
        table.setDefaultEditor(EmploymentCategory.class,new EmploymentCategoryEditor());
        table.setRowHeight(25);

        JMenuItem removeItem = new JMenuItem("Delete Row");
        popup.add(removeItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int row = table.rowAtPoint(e.getPoint());

                table.getSelectionModel().setSelectionInterval(row, row);

                if (e.getButton() == MouseEvent.BUTTON3) {

                    popup.show(table, e.getX(), e.getY());
                }
            }
        });

        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();

                if (personTableListner != null) {
                    personTableListner.rowDeleted(row);
                    tableModel.fireTableRowsDeleted(row, row);
                }
                System.out.println(row);
            }
        });

        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setData(List<Person> db) {
        tableModel.setData(db);
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    public void setPersonTableListner(PersonTableListner listner) {
        this.personTableListner = listner;
    }
}
