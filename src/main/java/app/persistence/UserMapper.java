package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.*;

public class UserMapper {


    public static void createUser(String firstName, String lastName, String email, String phone, String password1, int addressId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (first_name, last_name, email, phone, password, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, password1);
            ps.setInt(6, addressId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Fejl ved oprettelse af ny bruger");
            }
        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "E-mailen findes allerede. Vælg et andet";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }
}