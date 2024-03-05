package gui;

import model.EmploymentCategory;
import model.Person;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PersonTableModel extends AbstractTableModel {

    private List<Person> db;

    private String[] colNames = {"ID", "Name", "Occupation", "Age Category", "Employment Category", "US Citizen", "Tax ID"};

    public PersonTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 1:
                return true;
            case 4:
                return true;
            case 5:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {

        if (db==null) return;

        Person person = db.get(rowIndex);

        switch (columnIndex){
            case 1:
                person.setName((String) value);
                break;
            case 4:
                person.setEmpCat((EmploymentCategory) value);
                break;
            case 5:
                person.setUsCitizen((Boolean) value);
                break;
            default:
                return;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        switch (columnIndex){
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return EmploymentCategory.class;
                case 5:
                    return Boolean.class;
                case 6:
                    return String.class;

            default:
                return null;
            }
    }

    public void setData(List<Person> db) {
        this.db = db;
    }

    @Override
    public int getRowCount() {
        return db.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Person person = db.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return person.getId();
            case 1:
                return person.getName();
            case 2:
                return person.getOccupation();
            case 3:
                return person.getAgeCategory();
            case 4:
                return person.getEmpCat();
            case 5:
                return person.isUsCitizen();
            case 6:
                return person.getTaxId();
        }
        return null;
    }
}
