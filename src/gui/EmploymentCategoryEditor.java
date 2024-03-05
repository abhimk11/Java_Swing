package gui;

import model.EmploymentCategory;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public class EmploymentCategoryEditor extends AbstractCellEditor implements TableCellEditor {

    private JComboBox combo;

    public EmploymentCategoryEditor(){
        combo = new JComboBox<>(EmploymentCategory.values());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        combo.setSelectedItem(value);

        combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });

        return combo;
    }

    @Override
    public Object getCellEditorValue() {
        return combo.getSelectedItem();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }
}
