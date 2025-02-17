import java.sql.*;
import java.util.Scanner;

public class Lab17 {
	
	// update the following for your course database and your password for MySQL
	
	private static final String DBURL = "jdbc:mysql://localhost/course";
	private static final String DBUSERID = "root";
	private static final String DBPASSWORD = "1sCHqOOl25";

	public static void main(String[] args) {
		
		// prompt for student_id, name, department 
		// insert a row for the new student with tot_cred of 0
		// then list all students ID, name in the department
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter student id");
		String student_id = sc.nextLine();
		System.out.println("Enter name");
		String name = sc.nextLine();
		System.out.println("Enter department");
		String department = sc.nextLine();
		sc.close();

		try (Connection conn = DriverManager.getConnection( // create a connection to the course database
				DBURL,
				DBUSERID,
				DBPASSWORD);
		) {
			conn.setAutoCommit(false); // turn off auto commit

			if (studentExists(conn, student_id)) {
				System.out.println("Error: Student with this ID already exists.");
				return;
			}

			if (!departmentExists(conn, department)) {
				System.out.println("Error: Department does not exist.");
				return;
			}

			try (PreparedStatement pstmt = conn.prepareStatement("insert into student(id, name, dept_name, tot_cred) values (?, ?, ?, 0)")) {
				pstmt.setString(1, student_id);
				pstmt.setString(2, name);
				pstmt.setString(3, department);

				int number = pstmt.executeUpdate();
				System.out.println("Number of modified rows: " + number);
			}

			try (PreparedStatement pstmt = conn.prepareStatement("select id, name  from student where dept_name = ?")) {
				pstmt.setString(1, department);

				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {

					System.out.println("Student ID : " + rs.getString(1) + ", Name : " + rs.getString(2));
				}
			}

			conn.commit(); // commit or rollback the transaction
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e);
			try (Connection conn = DriverManager.getConnection(
					DBURL,
					DBUSERID,
					DBPASSWORD);) {
				conn.rollback(); // commit or rollback the transaction
			} catch (SQLException ex) {
				System.out.println("Rollback Exception: " + ex);
			}
		}
	}

	private static boolean studentExists(Connection conn, String student_id) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM student WHERE id = ?")) {
			pstmt.setString(1, student_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next(); // returns true if a row is found
			}
		}
	}

	private static boolean departmentExists(Connection conn, String department) throws SQLException {
		try (PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM department WHERE dept_name = ?")) {
			pstmt.setString(1, department);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next(); // returns true if a row is found
			}
		}
	}
}
