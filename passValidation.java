package userValidation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class passValidation {

	final static String url = "jdbc:mysql://localhost:3306/user";
	final static String user = "root";
	final static String pwd = "root";

	static Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, user, pwd);
		return con;
	}

	public static boolean isPassValid(String password) {
		int len = password.length();
		boolean passwordAuthentication = true;
		Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
		Pattern lowerCasePatten = Pattern.compile("[a-z ]");
		Pattern numaricPatten = Pattern.compile("[0-9 ]");
		Pattern passwordSpecialChar = Pattern.compile("[@#$%&*/\\\\]", Pattern.CASE_INSENSITIVE);

		if (password.length() < 9) {
			System.out.println("Password is too Short.Please enter at least 9 character");
			passwordAuthentication = false;
		}
		if (!passwordSpecialChar.matcher(password).find()) {
			System.out.println(" Please enter valid Special symbole Like @ # $ % & * / \\ ");
			passwordAuthentication = false;
		}
		if (!UpperCasePatten.matcher(password).find()) {
			System.out.println("Please enter at least one upperCase Character");
			passwordAuthentication = false;

		}
		if (!lowerCasePatten.matcher(password).find()) {
			System.out.println("Please enter at least one lowerCase character");
			passwordAuthentication = false;
		}
		if (!numaricPatten.matcher(password).find()) {
			System.out.println("Please enter at least one numeric number 0 to 9");
			passwordAuthentication = false;
		}
		return passwordAuthentication;

	}

	public static String getCurrentDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String currentDate = formatter.format(date);
		return currentDate;

	}

//For login Authentication and weather password is expired or not checking.
	public static int loginAuthentication(String password, String userName) throws SQLException {
		String date = "";
		String oldDate = "";
		String name = "";
		int id = 0;
		Connection con = null;
		String password1 = "";
		String query1 = "select * from login where userName=?";
		List list = new ArrayList<String>();
		try {
			con = getConnection();
			PreparedStatement stm = con.prepareStatement(query1);
			stm.setString(1, userName);
			ResultSet result = stm.executeQuery();
			while (result.next()) {
				id = result.getInt("userId");
				password1 = result.getString("userPassword");
				name = result.getString("userName");
				date = result.getString("userPswChangedDate");
				list.add(date);
			}
			Object[] str = list.toArray();
			Arrays.sort(str);
			oldDate = (String) str[str.length - 1];
			if (isPasswordExpired(oldDate, getCurrentDate()) == true) {
				char choice = 'Y';
				System.out.println("oops!!..Your password has been expired.Please update your password");

				System.out.println("You wish to change your password ."
						+ "\n Please enter Y for Update or N for You Dont Wish to update  ");
				Scanner scanner = new Scanner(System.in);
				choice = scanner.next().charAt(0);
				scanner.nextLine();
				if (choice == 'Y' || choice == 'y') {
					System.out.println("Please enter your new password");
					String newPassword1 = scanner.nextLine();

					if (isNotDefault(newPassword1) == true) {
						isPasswordDifferentFromLastFive(name, newPassword1, getCurrentDate(), id, password1);
					} else
						return 0;
				}

			} else
				isPasswordMatched(password1, name, oldDate);

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			con.close();
		}
		return 0;

	}

	public static boolean isPasswordExpired(String userLastPwsChangedDate, String userPwsChangedAndCurrentDate)
			throws ParseException {
		boolean isPassExpired = false;

		final int daysToCompare = 14;
		String[] userLastPwsChangedDateStr = userLastPwsChangedDate.split("/");
		String userLastPwsChangedDateStrNew = userLastPwsChangedDateStr[2] + userLastPwsChangedDateStr[1]
				+ userLastPwsChangedDateStr[0];
		int userLastPwsChangedDateInt = Integer.parseInt(userLastPwsChangedDateStrNew);
		String[] userPwsChangedCurrentDateStr = userPwsChangedAndCurrentDate.split("/");
		String userPwsChangedCurrentDateStrNew = userPwsChangedCurrentDateStr[2] + userPwsChangedCurrentDateStr[1]
				+ userPwsChangedCurrentDateStr[0];
		int userPwsChangedCurrentDateInt = Integer.parseInt(userPwsChangedCurrentDateStrNew);

		int noOfDays = userPwsChangedCurrentDateInt - userLastPwsChangedDateInt;

		if (noOfDays >= daysToCompare) {
			isPassExpired = true;
		} else
			isPassExpired = false;
		return isPassExpired;

	}

	public static void newUserRegister(String userPassword, String userName, String userPswChangedDate)
			throws SQLException {
		int userId = 0;
		Connection con1 = null;
		Connection con = null;
		if (isPassValid(userPassword) == true) {
			String query = "insert into login(userId,userPassword,userName,userPswChangedDate) values(?,?,?,?)";
			String Query = "select userId from login where userId=(select MAX(userId) from login)";

			try {
				con1 = getConnection();
				Statement stmt = con1.createStatement();
				ResultSet result = stmt.executeQuery(Query);
				result.next();
				int userId1 = result.getInt("userId");
				userId = userId1 + 1;

			} catch (Exception e) {
				System.out.println(e);
			}
			try {

				con = getConnection();
				PreparedStatement ps = con.prepareStatement(query);
				if (userId != 1) {
					ps.setInt(1, userId);
				} else {
					int userId2 = 100;
					ps.setInt(1, userId2);
				}
				ps.setString(2, userPassword);
				ps.setString(3, userName);
				ps.setString(4, userPswChangedDate);
				int RowsAffected = ps.executeUpdate();

				if (RowsAffected != 1) {
					System.out.print("Row NOt found");
				} else {
					System.out.print("You have registered successfully !!");
				}

			} catch (Exception e) {
				System.out.print(e);
			} finally {
				con1.close();
				con.close();
			}

		} else
			System.out.println("oops!!.. Wrong password ");

	}

	public static boolean isNotDefault(String password) {
		boolean Default = true;
		String defaultPassword = "Sunny@123";
		if (password.equals(defaultPassword)) {
			System.out.println("New password could not be same as default.Please enter different password");
			Default = false;
		}
		return Default;
	}

	public static void isPasswordDifferentFromLastFive(String oldUserName, String newPassword, String currentDate,
			int userId, String oldPassword) throws SQLException {

		String Query = "select userPassword from login where userName = ?";
		String query = "insert into login(userId,userPassword,userName,userPswChangedDate) values(?,?,?,?)";

		List<String> list = new ArrayList<String>();
		int count = 0;
		int temp = 0;
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement stmt = con.prepareStatement(Query);
			stmt.setString(1, oldUserName);

			ResultSet result = stmt.executeQuery();

			while (result.next()) {
				String password1 = result.getString("userPassword");
				list.add(password1);
			}

			for (String s : list) {
				temp++;
				if (s.equals(newPassword)) {
					count++;
				}
			}
			if (count > 0 && temp >= 5) {
				System.out.println(
						"Wrong choice !!...\n Your password is matched with new password.\n Please choose different password");

			} else if (temp < 5) {
				Connection con1 = null;
				try {
					con1 = getConnection();
					PreparedStatement ps = con1.prepareStatement(query);

					ps.setInt(1, userId);
					ps.setString(2, newPassword);
					ps.setString(3, oldUserName);
					ps.setNString(4, currentDate);
					int RowsAffected = ps.executeUpdate();

					if (RowsAffected != 1) {
						System.out.print("Row NOt found");
					} else {
						System.out.print("You have successfully updated your data!!");
					}

				} catch (Exception e) {
					System.out.print(e);
				} finally {
					con.close();
				}
			}

			else {
				Connection con1 = null;
				try {
					String sql = "update login SET userPassword = ?,userPswChangedDate=? WHERE  userPassword=? and userName=?";
					con1 = getConnection();
					PreparedStatement ps = con1.prepareStatement(sql);
					ps.setString(1, newPassword);
					ps.setString(2, currentDate);
					ps.setString(3, oldPassword);
					ps.setString(4, oldUserName);
					ps.executeUpdate();
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					con1.close();
				}
				System.out.println("You have updated  your password successfully...");
			}
		} catch (Exception e) {

		} finally {
			con.close();
		}
	}

	public static int isPasswordMatched(String password1, String userName, String date) throws SQLException {
		int i = 0;
		String passwordFromDB = "";
		Connection con = null;
		String query1 = "select * from login where userName=? and userPswChangedDate=?";
		try {
			con = getConnection();
			PreparedStatement stm = con.prepareStatement(query1);
			stm.setString(1, userName);
			stm.setString(2, date);
			ResultSet result = stm.executeQuery();
			while (result.next()) {
				passwordFromDB = result.getString("userPassword");
			}
			if (passwordFromDB.equals(password1)) {
				System.out.println("Welcome " + userName + "!!" + " You have logged in successfully...");
			} else {
				do {
					i++;
					System.out.println("You entered wrong password..Please try again!");
					System.out.println("Enter your password");

					Scanner scanner = new Scanner(System.in);
					String userEnteredPassword = scanner.nextLine();
					if (passwordFromDB.equals(userEnteredPassword)) {
						System.out.println("logged in");
						return 0;
					} else {
						System.out.println("Wrong password ");
					}
				} while (i < 4);
				System.out.println("Maximum number of attempts exceeded");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return 0;
	}

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter userName");
		String userName = scanner.nextLine();
		System.out.println("Please Enter your password");
		String pswr = scanner.nextLine();
		System.out.println("Please enter your choice L for Login and R to Register");
		char choice = scanner.next().charAt(0);
		String currentDate = getCurrentDate();
		if (choice == 'l' || choice == 'L') {
			loginAuthentication(pswr, userName);
		} else {
			if (isPassValid(pswr) && isNotDefault(pswr)) {
				newUserRegister(pswr, userName, currentDate);
			}
		}

	}

}
