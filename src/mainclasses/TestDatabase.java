package mainclasses;

import model.*;

import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("Running database test");

        Database db = new Database();
        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.addPerson(new Person("Abhinandan", "Software Engineer", AgeCategory.adult, EmploymentCategory.employed, "777", true, Gender.male));
        db.addPerson(new Person("Dawn", "builder", AgeCategory.adult, EmploymentCategory.selfEmployed, null, false, Gender.female));

        try {
            db.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.load();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.disconnect();
    }
}
