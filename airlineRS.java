import java.sql.*;
import java.util.Scanner;

public class airlineRS {

    static Connection conn = null;
    static Statement stmt = null;
    ResultSetMetaData rd;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline?characterEncoding=utf8","root","");
            stmt = conn.createStatement();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Book a flight");
                System.out.println("2. View available flights");
                System.out.println("3. Cancel reservation");
                System.out.println("4. View Booking");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        bookFlight(scanner);
                        break;
                    case 2:
                        viewFlights();
                        break;
                    case 3:
                        cancelReservation(scanner);
                        break;
                    case 4:
                        dispBooking();
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        if (conn != null)
                            conn.close();
                        return;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        } catch (SQLException se) {
               System.out.println(se);
        } catch (Exception e) {
            System.out.println(e);
        } 
        finally 
        {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                System.out.println(se);
            }
        }
    }

    private static void bookFlight(Scanner scanner) throws SQLException {
        System.out.println("Enter passenger name:");
        String passengerName = scanner.next();
        System.out.println("Enter origin:");
        String origin = scanner.next();
        System.out.println("Enter the destination:");
        String dest = scanner.next();
        System.out.println("Enter email-id:");
        String email = scanner.next();
        System.out.println("Enter the flight number:");
        String flightNumber = scanner.next();
        System.out.println("Enter passport no:");
        String pass = scanner.next();
        boolean flightExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM flight WHERE flight_number = '" + flightNumber + "' and origin= '" + origin + "'");
        if (rs.next()) {
            flightExists = true;
        }
    
        if (flightExists) {
            String seatNumber = generateSeatNumber();
            System.out.println("Your seat number is: " + seatNumber);
    
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO bookings (passenger_name, origin, dest, email, flight_number, pass, seat_number) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, passengerName);
            preparedStatement.setString(2, origin);
            preparedStatement.setString(3, dest);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, flightNumber);
            preparedStatement.setString(6, pass);
            preparedStatement.setString(7, seatNumber); 
            int rowsInserted = preparedStatement.executeUpdate();
    
            if (rowsInserted > 0) {
                System.out.println("Flight booked successfully for " + passengerName + " on flight " + flightNumber + " with seat number " + seatNumber);
            } else {
                System.out.println("Failed to book the flight.");
            }
        } else {
            System.out.println("Incorrect detials for booking");
        }
    }
    
    private static String generateSeatNumber() {
    
        return "A" + ((int) (Math.random() * 50) + 1); 
    }
    
    private static void viewFlights() throws SQLException {
        System.out.println("Available flights:");
        ResultSet rs = stmt.executeQuery("SELECT * FROM flight");
        ResultSetMetaData rd = rs.getMetaData();
        int cc = rd.getColumnCount();
        int i;
        for (i = 1; i <= cc; i++) {
            System.out.print(rd.getColumnName(i) + " |  ");
        }
        System.out.println(" ");
        while (rs.next()) {
            for (i = 1; i <= cc; i++) {
                System.out.print(rs.getString(i) + "   |        ");
            }
            System.out.println(" ");
        }
    }

   private static void cancelReservation(Scanner scanner) throws SQLException {
        System.out.println("Enter passenger name:");
        String passengerName = scanner.next();
        System.out.println("Enter flight number:");
        String flightNumber = scanner.next();
        System.out.println("Enter email-id:");
        String email=scanner.next();
        PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM bookings WHERE passenger_name = ? AND flight_number = ? AND email = ?");
        preparedStatement.setString(1, passengerName);
        preparedStatement.setString(2, flightNumber);
        preparedStatement.setString(3, email);
        int rowsDeleted = preparedStatement.executeUpdate();
    
        if (rowsDeleted > 0) {
            System.out.println("Reservation cancelled successfully for " + passengerName + " on flight " + flightNumber);
        } else {
            System.out.println("Failed to cancel the reservation.");
        }
    }
    private static void dispBooking() throws SQLException{
        System.out.println("Bookings:");
        ResultSet rs = stmt.executeQuery("SELECT * FROM bookings");
        ResultSetMetaData rd = rs.getMetaData();
        int cc = rd.getColumnCount();
        int i;
        for (i = 1; i <= cc; i++) {
            System.out.print(rd.getColumnName(i) + " |   ");
        }
        System.out.println(" ");
        while (rs.next()) {
            for (i = 1; i <= cc; i++) {
                System.out.print(rs.getString(i) + "  |   ");
            }
            System.out.println(" ");
        }
    
    }
}
    