package model;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {
    private LinkedList<Person> people;

    private Connection con;

    private int port;
    private String user;
    private String password;

    public Database() {
        people = new LinkedList<>();
    }

    public void configure(int port,String user,String password) throws Exception {
        this.port=port;
        this.user=user;
        this.password=password;

        if (con!=null){
            disconnect();
            connect();
        }
    }

    public void connect() throws Exception {
        if (con != null) return;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new Exception("Driver not found");
        }

        String url = "jdbc:mysql://localhost:3306/swingtest";

        con = DriverManager.getConnection(url, "root", "root");

        System.out.println("Connected: " + con);
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Can't close connection");
            }
        }
    }

    public void save() throws SQLException {
        String checkSql = "select count(*) as count from people where id=?";
        PreparedStatement checkStmt = con.prepareStatement(checkSql);

        String insertSql = "insert into people (id,name,age,employment_status,tax_id,us_citizen,gender,occupation) values (?,?,?,?,?,?,?,?)";
        PreparedStatement insertStatement = con.prepareStatement(insertSql);

        String updateSql = "update people set name=?,age=?,employment_status=?,tax_id=?,us_citizen=?,gender=?,occupation=? where id=?";
        PreparedStatement updateStatement = con.prepareStatement(updateSql);


        for (Person person : people) {
            int id = person.getId();
            String name = person.getName();
            String occupation = person.getOccupation();
            AgeCategory age = person.getAgeCategory();
            EmploymentCategory emp = person.getEmpCat();
            String tax = person.getTaxId();
            boolean isUs = person.isUsCitizen();
            Gender gender = person.getGender();

            checkStmt.setInt(1, id);
            ResultSet checkResult = checkStmt.executeQuery();
            checkResult.next();

            int count = checkResult.getInt(1);

            if (count == 0) {
                System.out.println("Inserting person with ID " + id);

                int col = 1;
                insertStatement.setInt(col++, id);
                insertStatement.setString(col++, name);
                insertStatement.setString(col++, age.name());
                insertStatement.setString(col++, emp.name());
                insertStatement.setString(col++, tax);
                insertStatement.setBoolean(col++, isUs);
                insertStatement.setString(col++, gender.name());
                insertStatement.setString(col++, occupation);

                insertStatement.executeUpdate();
            } else {
                System.out.println("Updating person with ID " + id);

                int col = 1;
                updateStatement.setString(col++, name);
                updateStatement.setString(col++, age.name());
                updateStatement.setString(col++, emp.name());
                updateStatement.setString(col++, tax);
                updateStatement.setBoolean(col++, isUs);
                updateStatement.setString(col++, gender.name());
                updateStatement.setString(col++, occupation);
                updateStatement.setInt(col++, id);

                updateStatement.executeUpdate();
            }

            //System.out.println("Count for person with ID " + id + " is " + count);
        }
        insertStatement.close();
        updateStatement.close();
        checkStmt.close();

    }

    public void load() throws SQLException {
        people.clear();

        String sql = "select id,name,age,employment_status,tax_id,us_citizen,gender,occupation from people order by name";
        Statement selectStatement = con.createStatement();

        ResultSet results = selectStatement.executeQuery(sql);

        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String age = results.getString("age");
            String emp = results.getString("employment_status");
            String taxId = results.getString("tax_id");
            Boolean isUs = results.getBoolean("us_citizen");
            String gender = results.getString("gender");
            String occ = results.getString("occupation");

            Person person = new Person(id, name, occ, AgeCategory.valueOf(age), EmploymentCategory.valueOf(emp), taxId, isUs, Gender.valueOf(gender));
            people.add(new Person(id, name, occ, AgeCategory.valueOf(age), EmploymentCategory.valueOf(emp), taxId, isUs, Gender.valueOf(gender)));

            System.out.println(person);
        }

        results.close();
        selectStatement.close();
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public List<Person> getPeople() {
        return Collections.unmodifiableList(people);
    }

    public void saveToFile(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        Person[] persons = people.toArray(new Person[people.size()]);

        oos.writeObject(persons);

        oos.close();
    }

    public void loadFromFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);

        try {
            Person[] persons = (Person[]) ois.readObject();
            people.clear();
            people.addAll(Arrays.asList(persons));

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ois.close();
    }

    public void removePerson(int index) {
        people.remove(index);
    }
}
