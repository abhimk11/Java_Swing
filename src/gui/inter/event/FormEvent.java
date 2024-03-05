package gui.inter.event;

import java.util.EventObject;

public class FormEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */

    private String name;
    private String occupation;
    private int ageCategory;
    private String empCat;
    private String taxId;
    private boolean usCitizen;
    private String gender;

    public FormEvent(Object source) {
        super(source);
    }

    public FormEvent(Object source, String name, String occupation, int ageCat, String empCat, String taxId, boolean usCitizen, String gender) {
        super(source);
        this.name = name;
        this.occupation = occupation;
        this.ageCategory = ageCat;
        this.empCat = empCat;
        this.usCitizen = usCitizen;
        this.taxId = taxId;
        this.gender = gender;
    }

    public int getAgeCategory() {
        return ageCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpCat() {
        return empCat;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getTaxId() {
        return taxId;
    }

    public boolean isUsCitizen() {
        return usCitizen;
    }

    public String getGender() {
        return gender;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
