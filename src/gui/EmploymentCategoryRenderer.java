package gui;

import model.EmploymentCategory;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EmploymentCategoryRenderer implements TableCellRenderer {

    private JComboBox combo;

    public EmploymentCategoryRenderer(){
        combo= new JComboBox<>(EmploymentCategory.values());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        combo.setSelectedItem(value);

        return combo;
    }
}
