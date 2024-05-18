package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminMapper {

    /**
     * Retrieves all orders from the database, ordered by status.
     *
     * @param connectionPool The connection pool for database connections.
     * @return A list of all orders.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders INNER JOIN status USING(status_id) ORDER BY status_id";
        List<Order> orderList = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = UserMapper.getUserById(userId, connectionPool);
                if (user == null) {
                    throw new DatabaseException("No user found with the provided userId: " + userId);
                }
                int orderId = rs.getInt("order_id");
                int cpLength = rs.getInt("cp_length");
                int cpWidth = rs.getInt("cp_width");
                int shLength = rs.getInt("shed_length");
                int shWidth = rs.getInt("shed_width");
                int statusId = rs.getInt("status_id");
                String statusName = rs.getString("status");
                String cpRoof = rs.getString("cp_roof");
                double price = rs.getDouble("price");

                // Create an Order object and add it to the list
                Order order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId, statusName);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all orders");
        }
        return orderList;
    }

    /**
     * Retrieves orders from the database by their status.
     *
     * @param statusId       The status ID to filter orders by.
     * @param connectionPool The connection pool for database connections.
     * @return A list of orders with the specified status.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<Order> getOrderByStatus(int statusId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders INNER JOIN status USING(status_id) WHERE status_id = ?";
        List<Order> orderList = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = UserMapper.getUserById(userId, connectionPool);
                if (user == null) {
                    throw new DatabaseException("No user found with the provided userId: " + userId);
                }
                int orderId = rs.getInt("order_id");
                int cpLength = rs.getInt("cp_length");
                int cpWidth = rs.getInt("cp_width");
                int shLength = rs.getInt("shed_length");
                int shWidth = rs.getInt("shed_width");
                String statusName = rs.getString("status");
                String cpRoof = rs.getString("cp_roof");
                double price = rs.getDouble("price");

                // Create an Order object and add it to the list
                Order order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId, statusName);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving orders by status");
        }
        return orderList;
    }

    /**
     * Retrieves the details of an order by its ID.
     *
     * @param orderId        The ID of the order to retrieve.
     * @param connectionPool The connection pool for database connections.
     * @return The Order object with the specified ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static Order getOrderDetailsById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders INNER JOIN status USING(status_id) WHERE order_id = ? ORDER BY status_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = UserMapper.getUserById(userId, connectionPool);
                    if (user == null) {
                        throw new DatabaseException("No user found with the provided userId: " + userId);
                    }
                    int cpLength = rs.getInt("cp_length");
                    int cpWidth = rs.getInt("cp_width");
                    int shLength = rs.getInt("shed_length");
                    int shWidth = rs.getInt("shed_width");
                    int statusId = rs.getInt("status_id");
                    String cpRoof = rs.getString("cp_roof");
                    double price = rs.getDouble("price");
                    String comment = rs.getString("comment");
                    String statusName = rs.getString("status");
                    int shippingId = rs.getInt("shipping_id");

                    // Return the Order object with the retrieved details
                    return new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId, statusName, comment, shippingId);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving order by ID", e.getMessage());
        }
        return null;
    }

    /**
     * Updates the details of an existing order.
     *
     * @param orderId        The ID of the order to update.
     * @param cpWidth        The new width of the carport.
     * @param cpLength       The new length of the carport.
     * @param shWidth        The new width of the shed.
     * @param shHeight       The new height of the shed.
     * @param cpRoof         The new roof type of the carport.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void updateOrder(int orderId, int cpWidth, int cpLength, int shWidth, int shHeight, String cpRoof, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET cp_width = ?, cp_length = ?, shed_width = ?, shed_length = ?, cp_roof = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            ps.setInt(1, cpWidth);
            ps.setInt(2, cpLength);
            ps.setInt(3, shWidth);
            ps.setInt(4, shHeight);
            ps.setString(5, cpRoof);
            ps.setInt(6, orderId);

            // Execute the update
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating order", e.getMessage());
        }
    }
}
