package Hostel;

import java.sql.*;

public class HostelDb {

    static void createDB(String dbname) {
        try {
            Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/","postgres","123");
            String query="CREATE DATABASE "+dbname; 
            Statement stmt=con.createStatement();
            stmt.executeLargeUpdate(query);
            System.out.println("Database "+dbname+"created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void createTblRooms(String dbname) {
        try {
            Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres","123");
            String query="CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_id SERIAL PRIMARY KEY," +
                    "room_number VARCHAR(10)," +
                    "room_type VARCHAR(50)," +
                    "capacity INT," +
                    "status VARCHAR(20))";
            Statement stmt=con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Rooms table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void createTblStudents(String dbname) {
        try {
            Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres","123");
            String query="CREATE TABLE IF NOT EXISTS students (" +
                    "student_id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100)," +
                    "age INT," +
                    "course VARCHAR(100)," +
                    "room_id INT REFERENCES rooms(room_id))";
            Statement stmt=con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Students table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void createTblFees(String dbname) {
        try {
            Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres","123");
            String query="CREATE TABLE IF NOT EXISTS fees (" +
                    "fee_id SERIAL PRIMARY KEY," +
                    "student_id INT REFERENCES students(student_id)," +
                    "amount DECIMAL(10, 2)," +
                    "paid BOOLEAN)";
            Statement stmt=con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Fees table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create 'maintenance' table
    static void createTblMaintenance(String dbname) {
        try {
            Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres","123");
            String query="CREATE TABLE IF NOT EXISTS maintenance (" +
                    "maintenance_id SERIAL PRIMARY KEY," +
                    "room_id INT REFERENCES rooms(room_id)," +
                    "description VARCHAR(255)," +
                    "status VARCHAR(20))";
            Statement stmt=con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Maintenance table created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRooms(String dbname) {
        String query="INSERT INTO rooms (room_number,room_type,capacity,status) VALUES (?,?,?,?)";
        try (Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,"postgres","123");
             PreparedStatement pstmt=con.prepareStatement(query)) {

            pstmt.setString(1,"101");
            pstmt.setString(2,"Single");
            pstmt.setInt(3,1);
            pstmt.setString(4,"available");
            pstmt.executeUpdate();

            pstmt.setString(1,"102");
            pstmt.setString(2,"Double");
            pstmt.setInt(3,2);
            pstmt.setString(4,"available");
            pstmt.executeUpdate();

            pstmt.setString(1,"103");
            pstmt.setString(2,"Single");
            pstmt.setInt(3,1);
            pstmt.setString(4,"available");
            pstmt.executeUpdate();

            pstmt.setString(1,"104");
            pstmt.setString(2,"Triple");
            pstmt.setInt(3,3);
            pstmt.setString(4,"available");
            pstmt.executeUpdate();
            
            pstmt.setString(1,"106");
            pstmt.setString(2,"Triple");
            pstmt.setInt(3,3);
            pstmt.setString(4,"occupied");
            pstmt.executeUpdate();
            
            System.out.println("Rooms added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        String dbname="hostel";
        
        // createDB(dbname);
        // createTblRooms(dbname);
        // createTblStudents(dbname);
        // createTblFees(dbname);
        // createTblMaintenance(dbname);
        
        addRooms(dbname);
    }
}
