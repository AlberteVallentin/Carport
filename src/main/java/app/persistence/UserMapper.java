package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    /**
     * Creates a new user in the database.
     *
     * @param firstName      The first name of the user.
     * @param lastName       The last name of the user.
     * @param email          The email of the user.
     * @param phone          The phone number of the user.
     * @param password1      The password of the user.
     * @param addressId      The address ID associated with the user.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void createUser(String firstName, String lastName, String email, String phone, String password1, int addressId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (first_name, last_name, email, phone_number, password, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
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
                msg = "E-mailen findes allerede. Vælg en anden e-mail eller log ind";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    /**
     * Authenticates a user based on email and password.
     *
     * @param email          The email of the user.
     * @param password       The password of the user.
     * @param connectionPool The connection pool for database connections.
     * @return The authenticated user.
     * @throws DatabaseException If authentication fails or a database error occurs.
     */
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

                return new User(userId, firstName, lastName, phoneNumber, email, password, isAdmin, addressId);
            } else {
                throw new DatabaseException("Login fejlede. Prøv igen.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Problemer med databasen. " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their ID from the database.
     *
     * @param userId         The ID of the user to retrieve.
     * @param connectionPool The connection pool for database connections.
     * @return The User object corresponding to the given ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static User getUserById(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phone_number");
                boolean isAdmin = rs.getBoolean("admin");
                int addressId = rs.getInt("address_id");

                return new User(userId, firstName, lastName, phoneNumber, email, null, isAdmin, addressId);
            } else {
                throw new DatabaseException("Brugeren er ikke fundet");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Problemer med databasen. " + e.getMessage());
        }
    }

    /**
     * Retrieves a user by their email and password from the database.
     *
     * @param email          The email of the user to retrieve.
     * @param password       The password of the user to retrieve.
     * @param connectionPool The connection pool for database connections.
     * @return The User object corresponding to the given email and password.
     * @throws DatabaseException If a database error occurs.
     */
    public static User getUserByEmailAndPassword(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                boolean isAdmin = rs.getBoolean("admin");
                int addressId = rs.getInt("address_id");
                user = new User(userId, firstName, lastName, phoneNumber, email, password, isAdmin, addressId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Email og password passer ikke sammen. Prøv igen", e.getMessage());
        }
        return user;
    }

    /**
     * Retrieves the address ID associated with a user ID.
     *
     * @param userId         The ID of the user.
     * @param connectionPool The connection pool for database connections.
     * @return The address ID associated with the given user ID.
     * @throws SQLException      If a SQL error occurs.
     * @throws DatabaseException If a database error occurs.
     */
    public static int getAddressIdByUserId(int userId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT address_id FROM users WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("address_id");
            } else {
                throw new DatabaseException("No address found for user with ID: " + userId);
            }
        } catch (SQLException e) {
            // Log exception details for debugging
            e.printStackTrace();
            throw new DatabaseException("Error getting address ID", e.getMessage());
        }
    }
}
