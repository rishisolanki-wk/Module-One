import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("********************************************************");
        System.out.println("       Welcome to the console based appilcation ");
        System.out.println("********************************************************");

        System.out.println("Admin Login");
        Scanner sc = new Scanner(System.in);
        String password = "null";
        System.out.println("Enter Password");
        while(true){
            System.out.print(":");
            password = sc.next();
            if(password.equals("123")) break;
            else System.out.println("WRONG PASSWORD! try again");
        }
        boolean exit = false;
        while (!exit){
            System.out.println("___________________________________________________________________");
            System.out.println("Click 1 to add User");
            System.out.println("Click 2 to view User");
            System.out.println("Click 3 to view all Users");
            System.out.println("Click 4 to delete User");
            System.out.println("Click any other to EXIT");
            System.out.print(":");
            int input = sc.nextInt();

            if(input == 1){
                addUser();
            } else if (input == 2) {
                System.out.print("Enter User Email : ");
                String userEmail = sc.next();
                showUser(userEmail);
            } else if (input == 3) {
                showUser(null);
            } else if (input == 4) {
                System.out.print("Enter User Email : ");
                String userEmail = sc.next();
                deleteUser(userEmail);
            } else{
                System.out.println("EXITED \nThank you. Byy!");
                exit = true;
            }
        }
    }

    private static void deleteUser(String userEmail) {
        String query =  "DELETE FROM users WHERE email = ?";
        String url = "jdbc:mysql://localhost:3306/module1";
        try {
            Connection con = DriverManager.getConnection(url, "root", "root");
            PreparedStatement ps = con.prepareStatement(query);
            if(userEmail != null){
                ps.setString(1, userEmail);
            }
            int row = ps.executeUpdate();
            if(row != 0){
                System.out.println("User Deleted Successfully!");
            } else {
                System.out.println("No Data Deleted!");
            }
            ps.close();
            con.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void showUser(String userEmail) {
        String query = userEmail == null ? "SELECT * FROM users" : "SELECT * FROM users WHERE email = ?";
        String url = "jdbc:mysql://localhost:3306/module1";
        try {
            Connection con = DriverManager.getConnection(url, "root", "root");
            PreparedStatement ps = con.prepareStatement(query);
            if(userEmail != null){
                ps.setString(1, userEmail);
            }
            ResultSet rs = ps.executeQuery();
            System.out.println("********************************************************");
            System.out.println("Users");
            System.out.println("___________________________________________________________________");
            int  i = 0;
            while(rs.next()){
                System.out.print(++i + "  |  ");
                System.out.print(rs.getString("name") + "  |  ");
                System.out.print(rs.getString("email") + "  |  ");
                ps = con.prepareStatement("SELECT * FROM phone_numbers WHERE user_id = ?");
                ps.setInt(1, rs.getInt(1));
                ResultSet rs1 = ps.executeQuery();
                while (rs1.next())
                System.out.print(rs1.getString("number") + "  |  ");


                System.out.println();
                System.out.println("___________________________________________________________________");
            }
            if (i == 0){
                throw new UserNotFoundException();
            }
            ps.close();
            con.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void addUser() {
        System.out.println("Enter User Details");
        Scanner sc = new Scanner(System.in);
        System.out.print("Name : ");
        String userName = sc.nextLine();
        System.out.print("Email : ");
        String userEmail = sc.next();
        while(!userEmail.endsWith("@gmail.com")){
            System.out.print("Invalid Email Enter Again : ");
            userEmail= sc.next();
        }
        List<String> numbers = new ArrayList<>();
        while(true){
            System.out.print("Mobile : ");
            String number = sc.next();
            while(number.replaceAll("[^0-9]","").length() != 10){
                System.out.print("Invalid Mobile Number Enter Again : ");
                number = sc.next();
            }
            numbers.add(number);
            System.out.println("To Add more Numbers press 1 : ");
            System.out.print(":");
            int add = sc.nextInt();
            if(add != 1){
                break;
            }
        }

        try {
            String url = "jdbc:mysql://localhost:3306/module1";
            Connection con = DriverManager.getConnection(url,"root","root");
            PreparedStatement ps = con.prepareStatement("INSERT INTO users(name, email) values(?, ?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userName);
            ps.setString(2, userEmail);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int row = 0;
            if(rs.next()){
                ps = con.prepareStatement("INSERT INTO phone_numbers(number, user_id) values(?, ?)");
                for(String number : numbers){
                    ps.setString(1, number);
                    ps.setInt(2, rs.getInt(1));
                    row = ps.executeUpdate();
                }
            }
            if(row != 0){
                System.out.println("User Registered Successfully!");
            } else {
                System.out.println("Something Went Wrong!");
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
