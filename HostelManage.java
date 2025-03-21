package Hostel;

import java.sql.*;
import java.util.Scanner;

public class HostelManage {
    static Connection con;

    static {
        try {
            con =DriverManager.getConnection("jdbc:postgresql://localhost:5432/hostel","postgres","123");
        } catch (SQLException e) {
            System.out.println("Error establishing connection: "+e.getMessage());
        }
    }

    private static void registerStudent(Scanner sc) {
        System.out.println("Enter student name: ");
        String name = sc.nextLine();

        System.out.println("Enter student age: ");
        int age = sc.nextInt();
        sc.nextLine();  
        System.out.println("Enter course: ");
        String course = sc.nextLine();

        System.out.println("Enter room number: ");
        String roomNumber = sc.nextLine();

        String checkRoomQuery = "SELECT * FROM rooms WHERE room_number=? AND status='avilable'";

        try (PreparedStatement checkRoomStmt = con.prepareStatement(checkRoomQuery)) {
            checkRoomStmt.setString(1, roomNumber);
            ResultSet rs = checkRoomStmt.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("room_id");
                String registerStudentQuery="INSERT INTO students(name,age,course,room_id) VALUES(?,?,?,?)";
                try (PreparedStatement stmt = con.prepareStatement(registerStudentQuery)) {
                    stmt.setString(1,name);
                    stmt.setInt(2,age);
                    stmt.setString(3,course);
                    stmt.setInt(4,roomId);

                    stmt.executeUpdate();
                    System.out.println("Student registered successfully.");

                    String updateRoomQuery = "UPDATE rooms SET status='occupied' WHERE room_id=?";
                    try (PreparedStatement updateRoomStmt = con.prepareStatement(updateRoomQuery)) {
                        updateRoomStmt.setInt(1, roomId);
                        updateRoomStmt.executeUpdate();
                    }
                }
            } else {
                System.out.println("Room not available.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void makePayment(Scanner sc) {
        System.out.println("Enter student ID: ");
        int studentId = sc.nextInt();

        System.out.println("Enter payment amount: ");
        double amount = sc.nextDouble();

        String checkFeeQuery = "SELECT * FROM fees WHERE student_id=? AND paid=false";
        try (PreparedStatement checkFeeStmt = con.prepareStatement(checkFeeQuery)) {
            checkFeeStmt.setInt(1, studentId);
            ResultSet rs = checkFeeStmt.executeQuery();

            if (rs.next()) {
              
                String updatePaymentQuery = "UPDATE fees SET amount=?, paid=true WHERE student_id=?";
                try (PreparedStatement updatePaymentStmt = con.prepareStatement(updatePaymentQuery)) {
                    updatePaymentStmt.setDouble(1, amount);
                    updatePaymentStmt.setInt(2, studentId);
                    updatePaymentStmt.executeUpdate();
                    System.out.println("Payment made successfully.");
                }
            } else {
                System.out.println("No outstanding fees for this student.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " +e.getMessage());
        }
    }
    private static void viewAllStudents() {
        String query = "SELECT * FROM students";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Student ID: " + rs.getInt("student_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Course: " + rs.getString("course"));
                System.out.println("Room ID: " + rs.getInt("room_id"));
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void deleteStudent(Scanner sc) {
        System.out.println("Enter student ID to delete: ");
        int studentId = sc.nextInt();

        String checkStudentQuery = "SELECT * FROM students WHERE student_id=?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkStudentQuery)) {
            checkStmt.setInt(1, studentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("room_id");
                if (roomId != 0) {
                    String updateRoomQuery = "UPDATE rooms SET status='available' WHERE room_id=?";
                    try (PreparedStatement updateRoomStmt = con.prepareStatement(updateRoomQuery)) {
                        updateRoomStmt.setInt(1, roomId);
                        updateRoomStmt.executeUpdate();
                        System.out.println("Room status updated to 'available'.");
                    }
                }

                String deleteStudentQuery = "DELETE FROM students WHERE student_id=?";
                try (PreparedStatement deleteStmt = con.prepareStatement(deleteStudentQuery)) {
                    deleteStmt.setInt(1, studentId);
                    deleteStmt.executeUpdate();
                    System.out.println("Student deleted successfully.");
                }
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public void reassignRoom(int studentId, int newRoomId) {
        try {
            String getCurrentRoomQuery="SELECT room_id FROM students WHERE student_id=?";
            PreparedStatement stmt1 =con.prepareStatement(getCurrentRoomQuery);
            stmt1.setInt(1, studentId);
            ResultSet rs1 = stmt1.executeQuery();
            
            if (rs1.next()) {
                int currentRoomId = rs1.getInt("room_id");

                String updateOldRoomQuery = "UPDATE rooms SET status='available' WHERE room_id=?";
                try (PreparedStatement stmt2 = con.prepareStatement(updateOldRoomQuery)) {
                    stmt2.setInt(1, currentRoomId);
                    stmt2.executeUpdate();
                }

                String checkNewRoomQuery = "SELECT * FROM rooms WHERE room_id=? AND status='available'";
                PreparedStatement stmt3 = con.prepareStatement(checkNewRoomQuery);
                stmt3.setInt(1, newRoomId);
                ResultSet rs2 = stmt3.executeQuery();

                if (rs2.next()) {
                  
                    String updateStudentQuery = "UPDATE students SET room_id=? WHERE student_id=?";
                    try (PreparedStatement stmt4 = con.prepareStatement(updateStudentQuery)) {
                        stmt4.setInt(1, newRoomId);
                        stmt4.setInt(2, studentId);
                        stmt4.executeUpdate();
                        System.out.println("Student reassigned to the new room successfully.");

                   
                        String updateNewRoomQuery = "UPDATE rooms SET status='occupied' WHERE room_id=?";
                        try (PreparedStatement stmt5 = con.prepareStatement(updateNewRoomQuery)) {
                            stmt5.setInt(1, newRoomId);
                            stmt5.executeUpdate();
                        }
                    }
                } else {
                    System.out.println("New room is not available.");
                }
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }
   
    private static void viewRoomStatus() {
        String query = "SELECT room_number, status FROM rooms";
        
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String roomNumber = rs.getString("room_number");
                String status = rs.getString("status");
                System.out.println("Room Number: " + roomNumber + " - Status: " + status);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HostelManage hm=new HostelManage();

        try {
           
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
        	
            System.out.println("PostgreSQL JDBC Driver not found.");
            return;
        }

      
        while (true) {
            System.out.println("Hostel Management System");
            System.out.println("1. Register Student");
            System.out.println("2. Make Payment");
            System.out.println("3. View All Students");
            System.out.println("4. Delete Student");
            System.out.println("5. Reassign Room to Student");
            System.out.println("6. View Room Status");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    registerStudent(sc);
                    break;
                case 2:
                    makePayment(sc);
                    break;
                case 3:
                    viewAllStudents();
                    break;
                case 4:
                    deleteStudent(sc);
                    break;
                case 5:
                    System.out.print("Enter student ID to reassign room: ");
                    int studentIdToReassign = sc.nextInt();
                    System.out.print("Enter new room ID to assign: ");
                    int newRoomId = sc.nextInt();
                    hm.reassignRoom(studentIdToReassign, newRoomId);
                    break;
                case 6:
                    hm.viewRoomStatus();  
                    break;
                case 7:
                    System.out.println("Exiting the system.");
                    try {
                        if (con != null) con.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing connection: " + e.getMessage());
                    }
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}