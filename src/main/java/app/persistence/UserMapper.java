package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;

public class UserMapper {


    public static void createUser(String firstName, String lastName, String email, int phone, String password1, int addressId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (first_name, last_name, email, phone_number, password, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setInt(4, phone);
            ps.setString(5, password1);
            ps.setInt(6, addressId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "E-mailen findes allerede. Vælg en anden e-mail eller log ind";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                boolean isAdmin = rs.getBoolean("admin");
                String phoneNumber = rs.getString("phone_number");
                int addressId = rs.getInt("address_id");

                return new User(userId, firstName, lastName, phoneNumber, email, password,isAdmin , addressId);
            } else {
                throw new DatabaseException("Login fejlede. Prøv igen.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Problemer med databasen. " + e.getMessage());
        }
    }
}